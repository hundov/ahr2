package mixin.blocks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import food.AHRFoodConsumption;
import food.cake.CakeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodConstants;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CakeBlock.class)
public abstract class CakeBlockMixin implements EntityBlock {

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CakeBlockEntity(pos, state);
    }

    @Inject(
            method = "useWithoutItem",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventZeroEfficiencyCake(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        if (!AHRFoodConsumption.canConsume(
                player,
                Items.CAKE
        )) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    @WrapOperation(
            method = "eat",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"
            )
    )
    private static void consumeCake(
            FoodData foodData,
            int nutrition,
            float saturationModifier,
            Operation<Void> original,
            @Local(argsOnly = true) Player player,
            @Local(argsOnly = true) BlockPos pos
    ) {
        float saturation =
                FoodConstants.saturationByModifier(
                        nutrition,
                        saturationModifier
                );

        AHRFoodConsumption.consume(
                player,
                player.level(),
                foodData,
                Items.CAKE,
                pos,
                nutrition,
                saturation
        );
    }
}