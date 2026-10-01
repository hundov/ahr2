package food;

import food.cake.CakeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import registry.AHRComponents;
import registry.AHRDamageTypes;

/**
 * Handles the consequences of eating spoiled food.
 * <p>
 * Responsible for:
 * - determining food age;
 * - checking shelf life;
 * - applying exhaustion and damage;
 * - displaying the spoiled food message;
 * - delegating status effects to AHRFoodEffects.
 * <p>
 * Does not create status effects or calculate normal food nutrition.
*/
public final class AHRSpoilage {

    // Exhaustion caused by spoiled food.
    private static final float MILD_EXHAUSTION = 120.0F;
    private static final float MODERATE_EXHAUSTION = 240.0F;
    private static final float SEVERE_EXHAUSTION = 360.0F;

    // Damage caused by spoiled food.
    private static final float MILD_DAMAGE = 2.0F;
    private static final float MODERATE_DAMAGE = 4.0F;
    private static final float SEVERE_DAMAGE = 8.0F;

    /**
     * Checks whether the consumed food has spoiled and applies its consequences.
     * The production day can come either from an item stack or from a cake block.
     */
    public static void apply(ServerPlayer player, Level level, Item item, ItemStack stack, BlockPos blockPos) {
        Integer madeOn = null;

        if (stack != null) madeOn = stack.get(AHRComponents.MADE_ON);
        else if (blockPos != null && level.getBlockEntity(blockPos) instanceof CakeBlockEntity cakeBlockEntity) {
            madeOn = cakeBlockEntity.getMadeOn();
        }

        if (madeOn == null) return;

        int currentDay = Math.toIntExact(level.getOverworldClockTime() / 24000L);
        int age = currentDay - madeOn;
        int shelfLife = AHRShelfLife.get(item);

        // Food is still fresh enough.
        if (age < shelfLife) return;

        player.causeFoodExhaustion(getSpoiledExhaustion(age, shelfLife));
        AHRFoodEffects.applySpoiledFoodEffects(player, level, age, shelfLife);

        player.sendOverlayMessage(
                Component.translatable("ahr2.food.spoiled_taste")
        );

        applySpoiledDamage(player, level, age, shelfLife);
    }

    /**
     * Determines exhaustion based on how far the food has exceeded its shelf life.
     */
    private static float getSpoiledExhaustion(int age, int shelfLife) {
        if (age >= shelfLife * 4) return SEVERE_EXHAUSTION;
        if (age >= shelfLife * 2) return MODERATE_EXHAUSTION;

        return MILD_EXHAUSTION;
    }

    /**
     * Applies damage based on the severity of the spoilage.
     */
    private static void applySpoiledDamage(ServerPlayer player, Level level, int age, int shelfLife) {
        float damage;

        if (age >= shelfLife * 4) damage = SEVERE_DAMAGE;
        else if (age >= shelfLife * 2) damage = MODERATE_DAMAGE;
        else damage = MILD_DAMAGE;

        DamageSource damageSource = new DamageSource(
                level.registryAccess()
                        .lookupOrThrow(Registries.DAMAGE_TYPE)
                        .getOrThrow(AHRDamageTypes.SPOILED_FOOD)
        );

        player.hurtServer((ServerLevel) level, damageSource, damage);
    }

    public static boolean isSpoiled(Level level, ItemStack stack) {
        if (stack.isEmpty()) return false;

        Integer madeOn = stack.get(AHRComponents.MADE_ON);
        if (madeOn == null) return false;

        int currentDay = Math.toIntExact(level.getOverworldClockTime() / 24000L);
        int age = currentDay - madeOn;
        int shelfLife = AHRShelfLife.get(stack.getItem());

        return age >= shelfLife;
    }

    private AHRSpoilage() {
    }
}