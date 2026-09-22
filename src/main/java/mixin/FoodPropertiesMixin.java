package mixin;

import component.AHRComponents;
import damage.AHRDamageTypes;
import food.AHRFoodTags;
import food.AHRShelfLife;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import network.AHRNetworking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import player.AHRAttachments;
import player.AHRFoodHistory;
import util.Logger;

@Mixin(FoodProperties.class)
public class FoodPropertiesMixin {

    @Unique
    private static final Logger LOG = new Logger();
    static {
        LOG.enabled = false;
    }

    @Unique
    private static final float AHR_SATURATION_MULTIPLAYER = 0.25F;

    @Redirect(
            method = "onConsume",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/food/FoodProperties;)V"
            )
    )
    private void modifyFood(
            FoodData foodData,
            FoodProperties foodProperties,
            Level level,
            LivingEntity user,
            ItemStack stack,
            Consumable consumable
    ) {
        if (!(user instanceof Player player)) {
            foodData.eat(foodProperties);
            return;
        }

        AHRFoodHistory history =
                player.getAttachedOrCreate(AHRAttachments.FOOD_HISTORY);

        int previousCount = history.count(stack.getItem());

        int efficiency = history.getEfficiency(stack.getItem());

        {
            if (efficiency == 50) {
                player.sendOverlayMessage(
                        Component.translatable("ahr2.food.efficiency_half")
                );
            }
        }

        int nutrition = (int) Math.floor(
                foodProperties.nutrition() * efficiency / 100.0
        );

        float saturation = foodProperties.saturation()
                * AHR_SATURATION_MULTIPLAYER
                * efficiency / 100.0F;

        if (nutrition > 0) {
            foodData.eat(
                    nutrition,
                    saturation
            );

            if (player instanceof ServerPlayer && stack.is(AHRFoodTags.RAW_FOOD)) applyRawFoodEffects(player, level);

        } else if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendOverlayMessage(
                    Component.translatable("ahr2.food.saturation_zero")
            );

            applyNegativeEffect(serverPlayer, level);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            Integer madeOn = stack.get(AHRComponents.MADE_ON);

            if (madeOn != null) {
                int currentDay = Math.toIntExact(level.getOverworldClockTime() / 24000L);
                int age = currentDay - madeOn;
                int shelfLife = AHRShelfLife.get(stack.getItem());

                if (age >= shelfLife) {
                    serverPlayer.causeFoodExhaustion(
                            getSpoiledExhaustion(age, shelfLife)
                    );

                    applySpoiledFoodEffects(
                            serverPlayer,
                            level,
                            age,
                            shelfLife
                    );

                    serverPlayer.sendOverlayMessage(
                            Component.translatable("ahr2.food.spoiled_taste")
                    );

                    applySpoiledDamage(serverPlayer, level, age, shelfLife);
                }
            }
        }

        {
            LOG.send("SIDE: " + (level.isClientSide() ? "CLIENT" : "SERVER"));
            LOG.send("Food: " + stack.getItem());
            LOG.send("Previous count: " + previousCount);
            LOG.send("Efficiency: " + efficiency + "%");
            LOG.send("Nutrition: " + foodProperties.nutrition() + " -> " + nutrition);
        }
    }

    @Unique
    private static void applyNegativeEffect(ServerPlayer player, Level level) {
        RandomSource random = level.getRandom();

        Holder<MobEffect> effect = switch (random.nextInt(4)) {
            case 0 -> MobEffects.SLOWNESS;
            case 1 -> MobEffects.NAUSEA;
            case 2 -> MobEffects.HUNGER;
            default -> MobEffects.POISON;
        };

        player.addEffect(
                createRandomFoodEffect(effect, random)
        );

    }

    @Unique
    private static void applyRawFoodEffects(Player player, Level level) {
        RandomSource random = level.getRandom();

        if (random.nextFloat() < 0.70F) {
            player.addEffect(
                    createRandomFoodEffect(
                            MobEffects.NAUSEA,
                            random
                    )
            );
        }

        if (random.nextFloat() < 0.70F) {
            player.addEffect(
                    createRandomFoodEffect(
                            MobEffects.HUNGER,
                            random
                    )
            );
        }

        if (random.nextFloat() < 0.50F) {
            player.addEffect(
                    createRandomFoodEffect(
                            MobEffects.POISON,
                            random
                    )
            );
        }
    }

    @Unique
    private static MobEffectInstance createRandomFoodEffect(Holder<MobEffect> effect, RandomSource random) {

        int durationSeconds = random.nextIntBetweenInclusive(15, 60);
        int amplifierRoll = random.nextInt(100);
        int amplifier;

        {
            if (amplifierRoll < 80) amplifier = 0;
            else if (amplifierRoll < 95) amplifier = 1;
            else amplifier = 2;
        }

        return new MobEffectInstance(
                effect,
                durationSeconds * 20,
                amplifier
        );

    }

    @Unique
    private static float getSpoiledExhaustion(int age, int shelfLife) {
        if (age >= shelfLife * 4) {
            return 360.0F;
        }

        if (age >= shelfLife * 2) {
            return 240.0F;
        }

        return 120.0F;
    }

    @Unique
    private static void applySpoiledFoodEffects(
            ServerPlayer player,
            Level level,
            int age,
            int shelfLife
    ) {
        RandomSource random = level.getRandom();

        if (age >= shelfLife * 4) {
            player.addEffect(
                    new MobEffectInstance(
                            MobEffects.POISON,
                            20 * random.nextIntBetweenInclusive(30, 120),
                            random.nextInt(3)
                    )
            );

            player.addEffect(
                    new MobEffectInstance(
                            MobEffects.HUNGER,
                            20 * random.nextIntBetweenInclusive(180, 360),
                            2
                    )
            );

            return;
        }

        if (age >= shelfLife * 2) {
            player.addEffect(
                    new MobEffectInstance(
                            MobEffects.NAUSEA,
                            20 * random.nextIntBetweenInclusive(60, 180),
                            0
                    )
            );

            if (random.nextFloat() < 0.5F) {
                player.addEffect(
                        new MobEffectInstance(
                                MobEffects.HUNGER,
                                20 * random.nextIntBetweenInclusive(60, 180),
                                1
                        )
                );
            }

            return;
        }

        player.addEffect(
                new MobEffectInstance(
                        MobEffects.NAUSEA,
                        20 * random.nextIntBetweenInclusive(30, 120),
                        0
                )
        );
    }

    @Unique
    private static void applySpoiledDamage(ServerPlayer player, Level level, int age, int shelfLife) {

        float damage;

        if (age >= shelfLife * 4) {
            damage = 8.0F;
        } else if (age >= shelfLife * 2) {
            damage = 4.0F;
        } else {
            damage = 2.0F;
        }

        DamageSource damageSource = new DamageSource(
                level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(AHRDamageTypes.SPOILED_FOOD)
        );

        player.hurtServer(
                (ServerLevel) level,
                damageSource,
                damage
        );
    }

    @Inject(
            method = "onConsume",
            at = @At("TAIL")
    )
    private void recordFood(
            Level level,
            LivingEntity user,
            ItemStack stack,
            Consumable consumable,
            CallbackInfo ci
    ) {
        if (!(user instanceof ServerPlayer player)) {
            return;
        }

        AHRFoodHistory history =
                player.getAttachedOrCreate(AHRAttachments.FOOD_HISTORY);

        AHRFoodHistory updated =
                history.add(stack.getItem(), level);

        player.setAttached(
                AHRAttachments.FOOD_HISTORY,
                updated
        );

        AHRNetworking.syncFoodHistory(
                player,
                updated
        );
    }
}