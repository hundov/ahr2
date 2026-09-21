package mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Block.class)
public class BlockMixin {

    @Redirect(
            method = "playerDestroy",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"
            )
    )
    private void modifyBlockBreakExhaustion(Player player, float _original, Level level, Player destroyingPlayer, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack destroyedWith) {
        float destroyProgress = state.getDestroyProgress(player, level, pos);
        float breakTime = 1.0f / destroyProgress;

        float exhaustion = Mth.clamp(
                0.0075F * breakTime,
                0.0075F,
                0.35F
        );

        player.causeFoodExhaustion(exhaustion);
    }
}
