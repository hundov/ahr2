package mixin;

import com.llamalad7.mixinextras.sugar.Local;
import food.cake.CakeBlockEntity;
import food.melon.MelonBlockEntity;
import food.mushroom.MushroomBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import registry.AHRComponents;
import util.IPerishableBlockEntity;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Inject(
            method = "place",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/Block;setPlacedBy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void addPlacementExhaustion(BlockPlaceContext placeContext, CallbackInfoReturnable<InteractionResult> cir) {
        Player player = placeContext.getPlayer();

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.causeFoodExhaustion(0.005F);
        }
    }

    @Inject(
            method = "place",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/Block;setPlacedBy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void transferMadeOn(
            BlockPlaceContext placeContext,
            CallbackInfoReturnable<InteractionResult> cir,
            @Local BlockPos pos,
            @Local Level level,
            @Local ItemStack itemStack
    ) {
        if (level.getBlockEntity(pos) instanceof IPerishableBlockEntity perishable) {
            Integer madeOn = itemStack.get(AHRComponents.MADE_ON);

            if (madeOn != null) {
                perishable.setMadeOn(madeOn);
            }
        }
    }
}
