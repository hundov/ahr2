package mixin;

import food.AHRFoodConsumption;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FoodProperties.class)
public class FoodPropertiesMixin {

    @Redirect(
            method = "onConsume",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/food/FoodProperties;)V"
            )
    )
    private void consumeFood(
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

        AHRFoodConsumption.consume(
                player,
                level,
                foodData,
                stack,
                foodProperties.nutrition(),
                foodProperties.saturation()
        );
    }
}