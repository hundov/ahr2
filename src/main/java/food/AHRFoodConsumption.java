package food;

import food.cake.CakeBlockEntity;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import registry.AHRAttachments;
import registry.AHRComponents;
import registry.AHRDamageTypes;
import registry.AHRFoodTags;
import registry.AHRNetworking;

public final class AHRFoodConsumption {

    public static final float SATURATION_MULTIPLIER = 0.25F;

    private AHRFoodConsumption() {
    }

    public static boolean canConsume(Player player, Item item) {
        AHRFoodHistory history =
                player.getAttachedOrCreate(AHRAttachments.FOOD_HISTORY);

        return history.getEfficiency(item) > 0;
    }

    public static void consume(
            Player player,
            Level level,
            FoodData foodData,
            ItemStack stack,
            int nutrition,
            float saturation
    ) {
        consume(
                player,
                level,
                foodData,
                stack.getItem(),
                stack,
                null,
                nutrition,
                saturation
        );
    }

    public static void consume(
            Player player,
            Level level,
            FoodData foodData,
            Item item,
            int nutrition,
            float saturation
    ) {
        consume(
                player,
                level,
                foodData,
                item,
                null,
                null,
                nutrition,
                saturation
        );
    }

    public static void consume(
            Player player,
            Level level,
            FoodData foodData,
            Item item,
            BlockPos blockPos,
            int nutrition,
            float saturation
    ) {
        consume(
                player,
                level,
                foodData,
                item,
                null,
                blockPos,
                nutrition,
                saturation
        );
    }

    private static void consume(
            Player player,
            Level level,
            FoodData foodData,
            Item item,
            ItemStack stack,
            BlockPos blockPos,
            int nutrition,
            float saturation
    ) {
        AHRFoodHistory history =
                player.getAttachedOrCreate(AHRAttachments.FOOD_HISTORY);

        int previousCount = history.count(item);
        int efficiency = history.getEfficiency(item);

        int adjustedNutrition = (int) Math.floor(
                nutrition * efficiency / 100.0
        );

        float adjustedSaturation =
                saturation
                        * SATURATION_MULTIPLIER
                        * efficiency
                        / 100.0F;

        if (efficiency == 50) {
            player.sendOverlayMessage(
                    Component.translatable("ahr2.food.efficiency_half")
            );
        }

        if (adjustedNutrition > 0) {
            foodData.eat(
                    adjustedNutrition,
                    adjustedSaturation
            );

            if (player instanceof ServerPlayer serverPlayer
                    && stack != null
                    && stack.is(AHRFoodTags.RAW_FOOD)) {
                applyRawFoodEffects(serverPlayer, level);
            }
        } else if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendOverlayMessage(
                    Component.translatable("ahr2.food.saturation_zero")
            );

            applyNegativeEffect(serverPlayer, level);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            applySpoilage(
                    serverPlayer,
                    level,
                    item,
                    stack,
                    blockPos
            );
        }

        if (player instanceof ServerPlayer serverPlayer) {
            AHRFoodHistory updated =
                    history.add(item, level);

            player.setAttached(
                    AHRAttachments.FOOD_HISTORY,
                    updated
            );

            AHRNetworking.syncFoodHistory(
                    serverPlayer,
                    updated
            );
        }
    }

    private static void applyNegativeEffect(
            ServerPlayer player,
            Level level
    ) {
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

    private static void applyRawFoodEffects(
            ServerPlayer player,
            Level level
    ) {
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

    private static MobEffectInstance createRandomFoodEffect(
            Holder<MobEffect> effect,
            RandomSource random
    ) {
        int durationSeconds =
                random.nextIntBetweenInclusive(15, 60);

        int amplifierRoll = random.nextInt(100);

        int amplifier;

        if (amplifierRoll < 80) {
            amplifier = 0;
        } else if (amplifierRoll < 95) {
            amplifier = 1;
        } else {
            amplifier = 2;
        }

        return new MobEffectInstance(
                effect,
                durationSeconds * 20,
                amplifier
        );
    }

    private static void applySpoilage(
            ServerPlayer player,
            Level level,
            Item item,
            ItemStack stack,
            BlockPos blockPos
    ) {
        Integer madeOn = null;

        if (stack != null) {
            madeOn = stack.get(AHRComponents.MADE_ON);
        } else if (blockPos != null
                && level.getBlockEntity(blockPos) instanceof CakeBlockEntity cakeBlockEntity) {
            madeOn = cakeBlockEntity.getMadeOn();
        }

        if (madeOn == null) {
            return;
        }

        int currentDay =
                Math.toIntExact(
                        level.getOverworldClockTime() / 24000L
                );

        int age = currentDay - madeOn;
        int shelfLife = AHRShelfLife.get(item);

        if (age < shelfLife) {
            return;
        }

        player.causeFoodExhaustion(
                getSpoiledExhaustion(age, shelfLife)
        );

        applySpoiledFoodEffects(
                player,
                level,
                age,
                shelfLife
        );

        player.sendOverlayMessage(
                Component.translatable("ahr2.food.spoiled_taste")
        );

        applySpoiledDamage(
                player,
                level,
                age,
                shelfLife
        );
    }

    private static float getSpoiledExhaustion(
            int age,
            int shelfLife
    ) {
        if (age >= shelfLife * 4) {
            return 360.0F;
        }

        if (age >= shelfLife * 2) {
            return 240.0F;
        }

        return 120.0F;
    }

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

    private static void applySpoiledDamage(
            ServerPlayer player,
            Level level,
            int age,
            int shelfLife
    ) {
        float damage;

        if (age >= shelfLife * 4) {
            damage = 8.0F;
        } else if (age >= shelfLife * 2) {
            damage = 4.0F;
        } else {
            damage = 2.0F;
        }

        DamageSource damageSource = new DamageSource(
                level.registryAccess()
                        .lookupOrThrow(Registries.DAMAGE_TYPE)
                        .getOrThrow(AHRDamageTypes.SPOILED_FOOD)
        );

        player.hurtServer(
                (ServerLevel) level,
                damageSource,
                damage
        );
    }
}