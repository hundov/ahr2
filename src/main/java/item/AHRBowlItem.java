package item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import registry.AHRItems;

public class AHRBowlItem extends Item {

    public AHRBowlItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        BlockHitResult hitResult = getPlayerPOVHitResult(
                level,
                player,
                ClipContext.Fluid.SOURCE_ONLY
        );

        if (hitResult.getType() == HitResult.Type.MISS) {
            return InteractionResult.PASS;
        }

        BlockPos pos = hitResult.getBlockPos();

        if (!level.getFluidState(pos).is(Fluids.WATER)) {
            return InteractionResult.PASS;
        }

        if (!level.getFluidState(pos).isSource()) {
            return InteractionResult.PASS;
        }

        if (!level.mayInteract(player, pos)) {
            return InteractionResult.FAIL;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ItemStack result = new ItemStack(AHRItems.BOWL_WITH_WATER);

        ItemStack transformed = ItemUtils.createFilledResult(
                itemStack,
                player,
                result
        );

        return InteractionResult.SUCCESS.heldItemTransformedTo(transformed);
    }
}