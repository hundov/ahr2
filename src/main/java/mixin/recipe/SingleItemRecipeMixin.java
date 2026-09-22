package mixin.recipe;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import recipe.AHRRecipeComponents;

@Mixin(SingleItemRecipe.class)
public class SingleItemRecipeMixin {

    @ModifyReturnValue(
            method = "assemble(Lnet/minecraft/world/item/crafting/SingleRecipeInput;)Lnet/minecraft/world/item/ItemStack;",
            at = @At("RETURN")
    )
    private ItemStack applyMadeOn(ItemStack result, SingleRecipeInput input) {
        return AHRRecipeComponents.applyMadeOn(result, input);
    }
}
