package mixin;

import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(FoodData.class)
public class FoodDataMixin {

    @ModifyConstant(
            method = "<init>",
            constant = @Constant(floatValue = 5.0F)
    )
    private float modifyInitialSaturation(float original) {
        return 0.0F;
    }

    @ModifyConstant(
            method = "readAdditionalSaveData",
            constant = @Constant(floatValue = 5.0F)
    )
    private float modifyDefaultSaturation(float original) {
        return 0.0F;
    }

}
