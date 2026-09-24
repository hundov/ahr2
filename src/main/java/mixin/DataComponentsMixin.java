package mixin;

import net.minecraft.core.component.DataComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(DataComponents.class)
public class DataComponentsMixin {

    @ModifyConstant(
            method = "lambda$static$1",
            constant = @Constant(intValue = 99)
    )
    private static int maxStackSizeCodec(int value) {
        return 16;
    }

    @ModifyConstant(
            method = "<clinit>",
            constant = @Constant(intValue = 64)
    )
    private static int defaultMaxStackSize(int value) {
        return 16;
    }
}