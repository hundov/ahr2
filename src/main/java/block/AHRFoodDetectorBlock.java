package block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import registry.AHRFoodTags;

public class AHRFoodDetectorBlock extends BaseEntityBlock {

    public AHRFoodDetectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(AHRFoodDetectorBlock::new);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AHRFoodDetectorBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof AHRFoodDetectorBlockEntity detector)) {
            return InteractionResult.PASS;
        }

        if (detector.isEmpty()) return InteractionResult.PASS;

        if (level.isClientSide()) return InteractionResult.SUCCESS;

        ItemStack extracted = detector.removeItemNoUpdate(0);

        if (extracted.isEmpty()) return InteractionResult.SUCCESS;

        if (player.getMainHandItem().isEmpty()) player.setItemInHand(InteractionHand.MAIN_HAND, extracted);
        else player.drop(extracted, false);

        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof AHRFoodDetectorBlockEntity detector)) {
            return InteractionResult.PASS;
        }

        if (!stack.is(AHRFoodTags.PERISHABLE_ITEM)) {
            if (detector.isEmpty()) return InteractionResult.PASS;

            if (level.isClientSide()) return InteractionResult.SUCCESS;

            ItemStack extracted = detector.removeItemNoUpdate(0);

            if (!extracted.isEmpty()) {
                if (player.getMainHandItem().isEmpty()) {
                    player.setItemInHand(InteractionHand.MAIN_HAND, extracted);
                } else {
                    player.drop(extracted, false);
                }
            }

            return InteractionResult.SUCCESS;
        }

        if (level.isClientSide()) return InteractionResult.SUCCESS;

        ItemStack held = player.getItemInHand(hand);

        if (detector.isEmpty()) {
            detector.setItem(0, held.copyWithCount(1));
            held.shrink(1);

            player.setItemInHand(
                    hand,
                    held.isEmpty() ? ItemStack.EMPTY : held
            );

            return InteractionResult.SUCCESS;
        }

        ItemStack stored = detector.getItem(0).copy();

        detector.setItem(0, held.copyWithCount(1));
        held.shrink(1);

        player.setItemInHand(hand, held.isEmpty() ? ItemStack.EMPTY : held);

        if (!player.addItem(stored)) player.drop(stored, false);

        return InteractionResult.SUCCESS;
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        if (direction == Direction.UP || direction == Direction.DOWN) {
            return 0;
        }

        if (level.getBlockEntity(pos) instanceof AHRFoodDetectorBlockEntity detector) {
            return detector.isFoodFresh() ? 15 : 0;
        }

        return 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return getSignal(state, level, pos, direction);
    }
}