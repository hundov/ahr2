package entity.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RemoveBlockGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;

public class ZombieDestroyWheatGoal extends RemoveBlockGoal {

    public ZombieDestroyWheatGoal(Block blockToRemove, PathfinderMob mob, double speedModifier, int verticalSearchRange) {
        super(blockToRemove, mob, speedModifier, verticalSearchRange);
    }

    @Override public void playDestroyProgressSound(LevelAccessor level, BlockPos pos) { level.playSound(null, pos, SoundEvents.GRASS_STEP, SoundSource.HOSTILE, 0.5f, 0.9f + this.mob.getRandom().nextFloat() * 0.2f); } @Override public void playBreakSound(Level level, BlockPos pos) { level.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.HOSTILE, 0.7f, 0.9f + this.mob.getRandom().nextFloat() * 0.2f); }

    @Override
    public double acceptedDistance() {
        return 2;
    }
}
