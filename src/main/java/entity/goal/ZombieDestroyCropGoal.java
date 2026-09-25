package entity.goal;

import block.AHRCropBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class ZombieDestroyCropGoal extends MoveToBlockGoal {

    private static final int WAIT_AFTER_BLOCK_FOUND = 20;

    private static final int UNREACHABLE_CONFIRMATION_TICKS = 40;

    private final PathfinderMob removerMob;

    private int ticksSinceReachedGoal;

    private int unreachableTicks;

    private double bestDistanceToTarget = Double.MAX_VALUE;

    public ZombieDestroyCropGoal(
            PathfinderMob mob,
            double speedModifier,
            int verticalSearchRange
    ) {
        super(mob, speedModifier, 24, verticalSearchRange);

        this.removerMob = mob;
    }

    @Override
    public boolean canUse() {
        if (!getServerLevel(this.removerMob)
                .getGameRules()
                .get(GameRules.MOB_GRIEFING)) {
            return false;
        }

        if (this.nextStartTick > 0) {
            --this.nextStartTick;
            return false;
        }

        if (this.findNearestBlock()) {
            this.nextStartTick = reducedTickDelay(WAIT_AFTER_BLOCK_FOUND);
            return true;
        }

        this.nextStartTick = this.nextStartTick(this.mob);
        return false;
    }

    @Override
    public void start() {
        super.start();

        this.ticksSinceReachedGoal = 0;
        this.unreachableTicks = 0;
        this.bestDistanceToTarget = Double.MAX_VALUE;
    }

    @Override
    public void stop() {
        super.stop();

        this.removerMob.fallDistance = 1.0D;

        this.unreachableTicks = 0;
        this.bestDistanceToTarget = Double.MAX_VALUE;
    }

    @Override
    public void tick() {
        super.tick();

        Level level = this.removerMob.level();
        BlockPos mobPos = this.removerMob.blockPosition();
        BlockPos cropPos = this.getPosWithCrop(mobPos, level);
        RandomSource random = this.removerMob.getRandom();

        this.updateUnreachableState();

        if (this.isReachedTarget() && cropPos != null) {
            if (this.ticksSinceReachedGoal > 0) {
                Vec3 movement = this.removerMob.getDeltaMovement();

                this.removerMob.setDeltaMovement(
                        movement.x,
                        0.3D,
                        movement.z
                );

                if (level instanceof ServerLevel serverLevel) {
                    BlockState cropState = level.getBlockState(cropPos);

                    serverLevel.sendParticles(
                            new BlockParticleOption(
                                    ParticleTypes.BLOCK,
                                    cropState
                            ),
                            cropPos.getX() + 0.5D,
                            cropPos.getY() + 0.7D,
                            cropPos.getZ() + 0.5D,
                            3,
                            (random.nextFloat() - 0.5D) * 0.08D,
                            (random.nextFloat() - 0.5D) * 0.08D,
                            (random.nextFloat() - 0.5D) * 0.08D,
                            0.15D
                    );
                }
            }

            if (this.ticksSinceReachedGoal % 2 == 0) {
                Vec3 movement = this.removerMob.getDeltaMovement();

                this.removerMob.setDeltaMovement(
                        movement.x,
                        -0.3D,
                        movement.z
                );

                if (this.ticksSinceReachedGoal % 6 == 0) {
                    this.playDestroyProgressSound(level, cropPos);
                }
            }

            if (this.ticksSinceReachedGoal > 60) {
                level.removeBlock(cropPos, false);

                if (level instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 20; ++i) {
                        double xa = random.nextGaussian() * 0.02D;
                        double ya = random.nextGaussian() * 0.02D;
                        double za = random.nextGaussian() * 0.02D;

                        serverLevel.sendParticles(
                                ParticleTypes.POOF,
                                cropPos.getX() + 0.5D,
                                cropPos.getY(),
                                cropPos.getZ() + 0.5D,
                                1,
                                xa,
                                ya,
                                za,
                                0.15D
                        );
                    }

                    this.playBreakSound(level, cropPos);
                }
            }

            ++this.ticksSinceReachedGoal;
        }
    }

    private void updateUnreachableState() {
        if (this.isReachedTarget()) {
            this.unreachableTicks = 0;
            this.bestDistanceToTarget = Double.MAX_VALUE;
            return;
        }

        double distanceToTarget = this.removerMob.distanceToSqr(
                this.blockPos.getX() + 0.5D,
                this.blockPos.getY() + 1.0D,
                this.blockPos.getZ() + 0.5D
        );

        if (distanceToTarget < this.bestDistanceToTarget) {
            this.bestDistanceToTarget = distanceToTarget;
            this.unreachableTicks = 0;
            return;
        }

        ++this.unreachableTicks;
    }

    public boolean isTargetUnreachable() {
        return this.unreachableTicks >= UNREACHABLE_CONFIRMATION_TICKS;
    }

    public void resetUnreachableState() {
        this.unreachableTicks = 0;
        this.bestDistanceToTarget = Double.MAX_VALUE;
    }

    protected void playDestroyProgressSound(
            LevelAccessor level,
            BlockPos pos
    ) {
        level.playSound(
                null,
                pos,
                SoundEvents.GENERIC_EAT.value(),
                SoundSource.HOSTILE,
                0.5F,
                0.9F + this.mob.getRandom().nextFloat() * 0.2F
        );
    }

    protected void playBreakSound(
            Level level,
            BlockPos pos
    ) {
        level.playSound(
                null,
                pos,
                SoundEvents.CROP_BREAK,
                SoundSource.HOSTILE,
                0.7F,
                0.9F + this.mob.getRandom().nextFloat() * 0.2F
        );
    }

    private @Nullable BlockPos getPosWithCrop(
            BlockPos pos,
            BlockGetter level
    ) {
        if (level.getBlockState(pos).getBlock() instanceof AHRCropBlock) {
            return pos;
        }

        BlockPos[] neighbours = new BlockPos[]{
                pos.below(),
                pos.west(),
                pos.east(),
                pos.north(),
                pos.south(),
                pos.below().below()
        };

        for (BlockPos neighborPos : neighbours) {
            if (level.getBlockState(neighborPos).getBlock()
                    instanceof AHRCropBlock) {
                return neighborPos;
            }
        }

        return null;
    }

    @Override
    protected boolean isValidTarget(
            LevelReader level,
            BlockPos pos
    ) {
        ChunkAccess chunk = level.getChunk(
                SectionPos.blockToSectionCoord(pos.getX()),
                SectionPos.blockToSectionCoord(pos.getZ()),
                ChunkStatus.FULL,
                false
        );

        if (chunk == null) {
            return false;
        }

        return chunk.getBlockState(pos).getBlock()
                instanceof AHRCropBlock
                && chunk.getBlockState(pos.above()).isAir()
                && chunk.getBlockState(pos.above(2)).isAir();
    }

    @Override
    public double acceptedDistance() {
        return 2.0D;
    }
}