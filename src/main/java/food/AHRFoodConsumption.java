package food;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import registry.AHRAttachments;
import registry.AHRFoodTags;
import registry.AHRNetworking;

/**
 * Handles the main food consumption process.
 * <p>
 * Responsible for:
 * - calculating food efficiency;
 * - applying difficulty and dimension food modifiers;
 * - applying final nutrition and saturation;
 * - triggering raw, zero-nutrition and spoiled food effects;
 * - updating food history and synchronizing it with the client.
 * <p>
 * Detailed spoilage and status-effect logic is delegated to dedicated classes.
*/
public final class AHRFoodConsumption {

    private static final float NETHER_FOOD_DIVISOR = 2.0F;
    private static final float END_FOOD_DIVISOR = 4.0F;

    public static float getNutritionMultiplier(Level level) {
        float multiplier = switch (level.getDifficulty()) {
            case EASY -> 0.8F;
            case NORMAL -> 0.7F;
            case HARD -> 0.4F;
            default -> 1.0F;
        };

        if (level.dimension() == Level.NETHER) multiplier /= NETHER_FOOD_DIVISOR;
        else if (level.dimension() == Level.END) multiplier /= END_FOOD_DIVISOR;

        return multiplier;
    }

    public static float getSaturationMultiplier(Level level) {
        float multiplier = switch (level.getDifficulty()) {
            case EASY -> 0.6F;
            case NORMAL -> 0.4F;
            case HARD -> 0.25F;
            default -> 0.9F;
        };

        if (level.dimension() == Level.NETHER) multiplier /= NETHER_FOOD_DIVISOR;
        else if (level.dimension() == Level.END) multiplier /= END_FOOD_DIVISOR;

        return multiplier;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean canConsume(Player player, Item item) {
        AHRFoodHistory history = AHRFoodHistory.get(player);
        return history.getEfficiency(item) > 0;
    }

    /**
     * Consumes an item stack and applies its food values.
     * Used for regular food items
     */
    public static void consume(Player player, Level level, FoodData foodData, ItemStack stack, int nutrition, float saturation) {
        consume(player, level, foodData, stack.getItem(), stack, null, nutrition, saturation);
    }

    /**
     * Consumes food without an item stack.
     * Used when only the food item type is known.
     */
    @SuppressWarnings("unused")
    public static void consume(Player player, Level level, FoodData foodData, Item item, int nutrition, float saturation) {
        consume(player, level, foodData, item, null, null, nutrition, saturation);
    }

    /**
     * Consumes food from a block at the specified position.
     * Used for block-based food, such as cake.
     */
    public static void consume(Player player, Level level, FoodData foodData, Item item, BlockPos blockPos, int nutrition, float saturation) {
        consume(player, level, foodData, item, null, blockPos, nutrition, saturation);
    }

    private static void consume(Player player, Level level, FoodData foodData, Item item, ItemStack stack, BlockPos blockPos, int nutrition, float saturation) {
        AHRFoodHistory history = AHRFoodHistory.get(player);
        int efficiency = history.getEfficiency(item);

        int adjustedNutrition = Math.round(nutrition
                        * getNutritionMultiplier(level)
                        * efficiency
                        / 100.0F
        );

        float adjustedSaturation = saturation
                        * getSaturationMultiplier(level)
                        * efficiency
                        / 100.0F;

        if (efficiency == 50) player.sendOverlayMessage(Component.translatable("ahr2.food.efficiency_half"));

        applyFood(player, level, foodData, stack, adjustedNutrition, adjustedSaturation);

        if (player instanceof ServerPlayer serverPlayer) {
            AHRSpoilage.apply(serverPlayer, level, item, stack, blockPos);

            AHRFoodHistory updated = history.add(item, level);
            player.setAttached(AHRAttachments.FOOD_HISTORY, updated);
            AHRNetworking.syncFoodHistory(serverPlayer, updated);
        }
    }

    private static void applyFood(Player player, Level level, FoodData foodData, ItemStack stack, int nutrition, float saturation) {
        if (nutrition > 0) {
            foodData.eat(nutrition, saturation);

            if (player instanceof ServerPlayer serverPlayer && stack != null && stack.is(AHRFoodTags.RAW_FOOD)) {
                AHRFoodEffects.applyRawFoodEffects(serverPlayer, level);
            }

            return;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendOverlayMessage(Component.translatable("ahr2.food.saturation_zero"));
            AHRFoodEffects.applyZeroNutritionEffect(serverPlayer, level);
        }
    }
}