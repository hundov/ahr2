package mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
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
        if (!(user instanceof Player player) || level.isClientSide()) {
            foodData.eat(foodProperties);
            return;
        }

        AHRFoodHistory history =
                player.getAttachedOrCreate(AHRAttachments.FOOD_HISTORY);

        int previousCount = history.count(stack.getItem());

        int efficiency = Math.max(
                0,
                100 - previousCount * 10
        );

        int nutrition = (int) Math.floor(
                foodProperties.nutrition() * efficiency / 100.0
        );

        int foodBefore = foodData.getFoodLevel();

        foodData.eat(
                nutrition,
                foodProperties.saturation()
        );

        int foodAfter = foodData.getFoodLevel();

        LOG.send("Food: " + stack.getItem());
        LOG.send("Previous count: " + previousCount);
        LOG.send("Efficiency: " + efficiency + "%");
        LOG.send("Nutrition: " + foodProperties.nutrition() + " -> " + nutrition);
        LOG.send("Food level: " + foodBefore + " -> " + foodAfter);
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
        if (!(user instanceof Player player) || level.isClientSide()) {
            return;
        }

        AHRFoodHistory history =
                player.getAttachedOrCreate(AHRAttachments.FOOD_HISTORY);

        player.setAttached(
                AHRAttachments.FOOD_HISTORY,
                history.add(stack.getItem(), 36)
        );
    }
}