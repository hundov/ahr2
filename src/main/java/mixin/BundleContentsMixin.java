package mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.item.component.BundleContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BundleContents.class)
public class BundleContentsMixin {

    @Unique
    private static final int AHR_BUNDLE_CAPACITY = 32;

    @ModifyExpressionValue(
            method = "getWeight",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemInstance;getMaxStackSize()I"
            )
    )
    private static int ahr$bundleCapacity(int maxStackSize) {
        return AHR_BUNDLE_CAPACITY;
    }
}