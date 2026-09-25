package entity.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import registry.AHRGameEvents;

public class ZombieUnreachableTargetGoal extends Goal {

    private static final int STUCK_TIME_TICKS = 200;
    private static final int GAME_EVENT_COOLDOWN_TICKS = 200;

    private static final double REQUIRED_PROGRESS_SQUARED = 0.25D;

    private final Zombie zombie;
    private final ZombieDestroyCropGoal cropGoal;
    private final ZombieBreakObstacleGoal obstacleGoal;

    private int stuckTicks;
    private int gameEventCooldownTicks;

    private double bestDistanceSquared = Double.MAX_VALUE;

    private boolean waitingForObstacle;

    public ZombieUnreachableTargetGoal(
            Zombie zombie,
            ZombieDestroyCropGoal cropGoal,
            ZombieBreakObstacleGoal obstacleGoal
    ) {
        this.zombie = zombie;
        this.cropGoal = cropGoal;
        this.obstacleGoal = obstacleGoal;
    }

    @Override
    public boolean canUse() {
        if (this.gameEventCooldownTicks > 0) {
            this.gameEventCooldownTicks--;
            return false;
        }

        return this.hasUnreachableTarget();
    }

    @Override
    public boolean canContinueToUse() {
        return this.gameEventCooldownTicks <= 0
                && this.hasUnreachableTarget();
    }

    @Override
    public void start() {
        this.stuckTicks = 0;
        this.bestDistanceSquared = Double.MAX_VALUE;
        this.waitingForObstacle = false;

        this.tryFindObstacle();
    }

    @Override
    public void tick() {
        if (this.gameEventCooldownTicks > 0) {
            return;
        }

        /*
         * Plants may have a physical obstacle that the zombie can break.
         * Give ZombieBreakObstacleGoal priority over the stuck timer.
         */
        if (this.waitingForObstacle) {
            if (this.tryFindObstacle()) {
                this.stuckTicks = 0;
                this.bestDistanceSquared = this.getTargetDistanceSquared();
                return;
            }

            this.waitingForObstacle = false;
            this.stuckTicks = 0;
            this.bestDistanceSquared = this.getTargetDistanceSquared();
            return;
        }

        double currentDistanceSquared =
                this.getTargetDistanceSquared();

        /*
         * Reaching a new closest point resets the timer.
         *
         * The zombie does not have to stand completely still.
         * It only has to make insufficient progress toward its target
         * for the entire timeout period.
         */
        if (currentDistanceSquared
                < this.bestDistanceSquared - REQUIRED_PROGRESS_SQUARED) {

            this.bestDistanceSquared = currentDistanceSquared;
            this.stuckTicks = 0;
            return;
        }

        this.stuckTicks++;

        if (this.stuckTicks < STUCK_TIME_TICKS) {
            return;
        }

        this.emitZombieStuckEvent();

        this.stuckTicks = 0;
        this.bestDistanceSquared = currentDistanceSquared;
        this.gameEventCooldownTicks = GAME_EVENT_COOLDOWN_TICKS;

        this.cropGoal.resetUnreachableState();
    }

    @Override
    public void stop() {
        this.stuckTicks = 0;
        this.bestDistanceSquared = Double.MAX_VALUE;
        this.waitingForObstacle = false;
    }

    private boolean hasUnreachableTarget() {
        if (this.isAnimalTarget()) {
            return true;
        }

        return this.cropGoal.isTargetUnreachable();
    }

    private boolean isAnimalTarget() {
        LivingEntity target = this.zombie.getTarget();

        if (!(target instanceof Animal)) {
            return false;
        }

        return !this.zombie.isWithinMeleeAttackRange(target);
    }

    private double getTargetDistanceSquared() {
        LivingEntity animalTarget = this.zombie.getTarget();

        if (animalTarget instanceof Animal) {
            return this.zombie.distanceToSqr(animalTarget);
        }

        BlockPos target = this.zombie.getNavigation().getTargetPos();

        if (target != null) {
            return this.zombie.distanceToSqr(
                    target.getX() + 0.5D,
                    target.getY() + 0.5D,
                    target.getZ() + 0.5D
            );
        }

        return Double.MAX_VALUE;
    }

    private boolean tryFindObstacle() {
        Path path = this.zombie.getNavigation().getPath();

        if (path == null || path.canReach()) {
            return false;
        }

        Node endNode = path.getEndNode();

        if (endNode == null) {
            return false;
        }

        BlockPos endPos = endNode.asBlockPos();
        BlockPos target = path.getTarget();

        int minX = Math.min(endPos.getX(), target.getX());
        int maxX = Math.max(endPos.getX(), target.getX());

        int minY = Math.min(endPos.getY(), target.getY());
        int maxY = Math.max(endPos.getY(), target.getY());

        int minZ = Math.min(endPos.getZ(), target.getZ());
        int maxZ = Math.max(endPos.getZ(), target.getZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);

                    if (pos.equals(target)) {
                        continue;
                    }

                    if (!this.zombie.level()
                            .getBlockState(pos)
                            .getCollisionShape(
                                    this.zombie.level(),
                                    pos
                            )
                            .isEmpty()) {

                        this.obstacleGoal.setObstacle(pos);
                        this.waitingForObstacle = true;
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void emitZombieStuckEvent() {
        this.zombie.level().gameEvent(
                this.zombie,
                BuiltInRegistries.GAME_EVENT.getOrThrow(
                        AHRGameEvents.ZOMBIE_STUCK_KEY
                ),
                this.zombie.position()
        );
    }
}