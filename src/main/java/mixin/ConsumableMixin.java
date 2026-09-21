package mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import player.AHRAttachments;
import player.AHRFoodHistory;

@Mixin(Consumable.class)
public class ConsumableMixin {

    @Inject(
            method = "canConsume",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventZeroEfficiencyFood(LivingEntity user, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!(user instanceof Player player) || !stack.has(DataComponents.FOOD)) return;

        AHRFoodHistory history = player.getAttachedOrCreate(AHRAttachments.FOOD_HISTORY);
        int efficiency = history.getEfficiency(stack.getItem());
        if (efficiency == 0) cir.setReturnValue(false);
    }

}
