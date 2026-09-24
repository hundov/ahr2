package mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import registry.AHRComponents;
import registry.AHRFoodTags;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(
            method = "inventoryTick",
            at = @At("HEAD")
    )
    private void setMadeOn(ItemStack itemStack, ServerLevel level, Entity owner, EquipmentSlot slot, CallbackInfo ci){
        if (!itemStack.is(AHRFoodTags.PERISHABLE_ITEM)) return;
        if (itemStack.has(AHRComponents.MADE_ON)) return;
        int currentDay = Math.toIntExact(level.getOverworldClockTime() / 24000L);
        itemStack.set(AHRComponents.MADE_ON, currentDay);
        System.out.println(
                "[AHR] MADE_ON (tick): "
                        + itemStack.getItem()
                        + " -> "
                        + itemStack.get(AHRComponents.MADE_ON)
        );
    }

}
