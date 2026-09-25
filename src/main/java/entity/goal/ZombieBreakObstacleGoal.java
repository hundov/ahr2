package entity.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;

public class ZombieBreakObstacleGoal extends Goal {

    private static final double MOVEMENT_SPEED = 1.0D;
    private static final double REACH_DISTANCE_SQUARED = 4.0D;

    private static final int BREAK_TIME_TICKS = 60;
    private static final int BREAK_SOUND_INTERVAL_TICKS = 6;
    private static final int PARTICLE_INTERVAL_TICKS = 2;

    private final PathfinderMob mob;

    private BlockPos obstaclePos;
    private int breakTicks;

    public ZombieBreakObstacleGoal(PathfinderMob mob) {
        this.mob = mob;

        this.setFlags(EnumSet.of(
                Flag.MOVE,
                Flag.LOOK
        ));
    }

    public void setObstacle(BlockPos pos) {
        this.obstaclePos = pos.immutable();
        this.breakTicks = 0;
    }

    @Override
    public boolean canUse() {
        return this.obstaclePos != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.obstaclePos != null;
    }

    @Override
    public void start() {
        this.breakTicks = 0;
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();

        this.obstaclePos = null;
        this.breakTicks = 0;
    }

    @Override
    public void tick() {
        BlockPos pos = this.obstaclePos;

        if (pos == null) {
            return;
        }

        Level level = this.mob.level();
        BlockState state = level.getBlockState(pos);

        if (state.isAir()) {
            this.clearObstacle();
            return;
        }

        double distanceSquared = this.mob.distanceToSqr(
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D
        );

        if (distanceSquared > REACH_DISTANCE_SQUARED) {
            this.mob.getNavigation().moveTo(
                    pos.getX() + 0.5D,
                    pos.getY(),
                    pos.getZ() + 0.5D,
                    MOVEMENT_SPEED
            );

            this.mob.getLookControl().setLookAt(
                    pos.getX() + 0.5D,
                    pos.getY() + 0.5D,
                    pos.getZ() + 0.5D
            );

            return;
        }

        this.mob.getNavigation().stop();

        this.mob.getLookControl().setLookAt(
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D
        );

        ++this.breakTicks;

        if (this.breakTicks % PARTICLE_INTERVAL_TICKS == 0
                && level instanceof ServerLevel serverLevel) {

            serverLevel.sendParticles(
                    new net.minecraft.core.particles.BlockParticleOption(
                            net.minecraft.core.particles.ParticleTypes.BLOCK,
                            state
                    ),
                    pos.getX() + 0.5D,
                    pos.getY() + 0.5D,
                    pos.getZ() + 0.5D,
                    3,
                    0.08D,
                    0.08D,
                    0.08D,
                    0.15D
            );
        }

        if (this.breakTicks % BREAK_SOUND_INTERVAL_TICKS == 0) {
            level.playSound(
                    null,
                    pos,
                    SoundEvents.GENERIC_EAT.value(),
                    SoundSource.HOSTILE,
                    0.5F,
                    0.9F + this.mob.getRandom().nextFloat() * 0.2F
            );
        }

        if (this.breakTicks >= BREAK_TIME_TICKS) {
            level.destroyBlock(
                    pos,
                    true,
                    this.mob
            );

            this.clearObstacle();
        }
    }

    private void clearObstacle() {
        this.mob.getNavigation().stop();

        this.obstaclePos = null;
        this.breakTicks = 0;
    }
}