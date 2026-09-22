package mixin.recipe;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import recipe.AHRRecipeComponents;

@Mixin({
        ShapedRecipe.class,
        ShapelessRecipe.class,
})
public class RecipeAssemblyMixin {

    @ModifyReturnValue(
            method = "assemble",
            at = @At("RETURN")
    )
    private ItemStack applyMadeOn(ItemStack result, CraftingInput input) {
        return AHRRecipeComponents.applyMadeOn(result, input);
    }
}
