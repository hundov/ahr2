package block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AHRCropBlockEntity extends BlockEntity {

    private int subStage = 0;
    private boolean boneMealUsed = false;
    private int deathChance = 0;

    public AHRCropBlockEntity(BlockPos pos, BlockState state) {
        super(AHRBlockEntities.CROP, pos, state);
    }

    public void tickGrowth(ServerLevel level, RandomSource random) {

        BlockState state = getBlockState();
        BlockPos pos = getBlockPos();

        if (state.getValue(AHRCropBlock.AGE) >= 7) {
            deathChance += 5;

            if (random.nextInt(100) < deathChance) {
                level.setBlock(pos, Blocks.DEAD_BUSH.defaultBlockState(), 2);
            }

            setChanged();
            return;
        }

        if (level.getRawBrightness(pos, 0) < 9) deathChance++;

        if (random.nextInt(100) < deathChance) {
            level.setBlock(pos, Blocks.DEAD_BUSH.defaultBlockState(), 2);
            return;
        }

        advanceSubStage(level);
    }

    public void applyBoneMeal(ServerLevel level) {

        if (boneMealUsed) {
            return;
        }

        deathChance += 3;
        boneMealUsed = true;
        advanceSubStage(level);
    }

    private void advanceSubStage(ServerLevel level) {
        subStage++;

        if (subStage >= 3) {
            subStage = 0;
            boneMealUsed = false;

            BlockState state = getBlockState();
            int age = state.getValue(AHRCropBlock.AGE);

            level.setBlock(
                    getBlockPos(),
                    state.setValue(AHRCropBlock.AGE, age + 1),
                    2
            );
        }

        setChanged();
    }

    public boolean isBoneMealUsed() {
        return boneMealUsed;
    }

    public int getSubStage() {
        return subStage;
    }

    public int getDeathChance() {
        return deathChance;
    }

}
