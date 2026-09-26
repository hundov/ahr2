package entity.goal;

import entity.PhantomAHRAccess;
import mixin.PhantomAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;

public class PhantomBombingGoal extends Goal {

    private static final double APPROACH_HEIGHT = 12.0D;
    private static final double APPROACH_DISTANCE = 2.0D;
    private static final double TARGET_DISTANCE = 2.0D;

    private static final float EXPLOSION_RADIUS = 3.0F;

    private final Phantom phantom;

    private boolean diving;

    public PhantomBombingGoal(Phantom phantom) {
        this.phantom = phantom;

        this.setFlags(EnumSet.of(
                Flag.MOVE,
                Flag.LOOK
        ));
    }

    @Override
    public boolean canUse() {
        return this.getBombingTarget() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.getBombingTarget() != null;
    }

    @Override
    public void start() {
        this.diving = false;
    }

    @Override
    public void tick() {
        BlockPos target = this.getBombingTarget();

        if (target == null) {
            return;
        }

        if (!this.diving) {
            this.moveAboveTarget(target);

            double distanceSquared =
                    this.phantom.distanceToSqr(
                            target.getX() + 0.5D,
                            target.getY() + APPROACH_HEIGHT,
                            target.getZ() + 0.5D
                    );

            if (distanceSquared <= APPROACH_DISTANCE * APPROACH_DISTANCE) {
                this.diving = true;
                this.diveIntoTarget(target);
            }

            return;
        }

        this.diveIntoTarget(target);

        if (this.phantom.horizontalCollision
                || this.phantom.verticalCollision) {
            this.explode();
            return;
        }

        double targetDistanceSquared =
                this.phantom.distanceToSqr(
                        target.getX() + 0.5D,
                        target.getY() + 0.5D,
                        target.getZ() + 0.5D
                );

        if (targetDistanceSquared <= TARGET_DISTANCE * TARGET_DISTANCE) {
            this.explode();
        }
    }

    @Override
    public void stop() {
        this.diving = false;
    }

    private @Nullable BlockPos getBombingTarget() {
        return ((PhantomAHRAccess) this.phantom)
                .ahr$getBombingTarget();
    }

    private void moveAboveTarget(BlockPos target) {
        Vec3 point = new Vec3(
                target.getX() + 0.5D,
                target.getY() + APPROACH_HEIGHT,
                target.getZ() + 0.5D
        );

        ((PhantomAccessor) this.phantom)
                .ahr$setMoveTargetPoint(point);
    }

    private void diveIntoTarget(BlockPos target) {
        Vec3 point = new Vec3(
                target.getX() + 0.5D,
                target.getY() + 0.5D,
                target.getZ() + 0.5D
        );

        ((PhantomAccessor) this.phantom)
                .ahr$setMoveTargetPoint(point);
    }

    private void explode() {
        if (!(this.phantom.level()
                instanceof ServerLevel level)) {
            return;
        }

        Vec3 position = this.phantom.position();

        level.explode(
                this.phantom,
                position.x,
                position.y,
                position.z,
                EXPLOSION_RADIUS,
                false,
                Level.ExplosionInteraction.BLOCK
        );

        ((PhantomAHRAccess) this.phantom)
                .ahr$clearBombingTarget();

        this.phantom.discard();
    }
}