package block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class AHRCropBlock extends CropBlock implements EntityBlock {

    public AHRCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new AHRCropBlockEntity(worldPosition, blockState);
    }

    // Main random tick - cycle
    @Override
    protected void randomTick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random
    ) {
        if (level.getBlockEntity(pos) instanceof AHRCropBlockEntity crop) {
            crop.tickGrowth(level, random);
        }
    }

    // random tick always active (for try to dead crop after full grow)
    @Override
    protected boolean isRandomlyTicking(final BlockState state) {
        return true;
    }

    // ------------------------
    // BONE MEAL
    // ------------------------

    @Override
    public void performBonemeal(final ServerLevel level, final RandomSource random, final BlockPos pos, final BlockState state) {
        if (level.getBlockEntity(pos) instanceof AHRCropBlockEntity crop) crop.applyBoneMeal(level);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        if (this.getAge(state) >= this.getMaxAge()) return false;
        if (level.getBlockEntity(pos) instanceof AHRCropBlockEntity crop) return !crop.isBoneMealUsed();
        return false;
    }

}
