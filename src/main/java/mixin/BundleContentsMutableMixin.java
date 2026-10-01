package mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(BundleContents.Mutable.class)
public class BundleContentsMutableMixin {

    @Redirect(
            method = "tryInsert",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;add(ILjava/lang/Object;)V",
                    ordinal = 0
            )
    )
    private void ahr$splitMergedStack(
            List<ItemStack> items,
            int index,
            Object object
    ) {
        ItemStack mergedStack = (ItemStack) object;
        int maxStackSize = mergedStack.getMaxStackSize();

        if (mergedStack.getCount() <= maxStackSize) {
            items.add(index, mergedStack);
            return;
        }

        int remaining = mergedStack.getCount();

        while (remaining > 0) {
            int amount = Math.min(remaining, maxStackSize);

            items.add(
                    index++,
                    mergedStack.copyWithCount(amount)
            );

            remaining -= amount;
        }
    }
}