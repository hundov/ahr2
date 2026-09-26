package entity.goal;

import entity.PhantomAHRAccess;
import mixin.PhantomAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class PhantomPatrolGoal extends Goal {

    private static final double PLAYER_SEARCH_RADIUS = 64.0D;

    private static final double PATROL_RADIUS_MIN = 6.0D;
    private static final double PATROL_RADIUS_MAX = 12.0D;

    private static final double PATROL_HEIGHT_MIN = 10.0D;
    private static final double PATROL_HEIGHT_MAX = 24.0D;

    private static final int TARGET_UPDATE_INTERVAL_TICKS = 40;

    private final Phantom phantom;

    private Vec3 patrolCenter = Vec3.ZERO;
    private int nextTargetTick;

    public PhantomPatrolGoal(Phantom phantom) {
        this.phantom = phantom;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.getBombingTarget() == null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.getBombingTarget() == null;
    }

    @Override
    public void start() {
        this.patrolCenter = this.phantom.position();
        this.nextTargetTick = 0;

        this.selectNextPoint();
    }

    @Override
    public void tick() {
        if (this.nextTargetTick > 0) {
            this.nextTargetTick--;
            return;
        }

        this.nextTargetTick = TARGET_UPDATE_INTERVAL_TICKS;

        this.updatePatrolCenter();
        this.selectNextPoint();
    }

    private void updatePatrolCenter() {
        if (!(this.phantom.level() instanceof ServerLevel level)) {
            return;
        }

        Player player = level.getNearestPlayer(
                this.phantom.getX(),
                this.phantom.getY(),
                this.phantom.getZ(),
                PLAYER_SEARCH_RADIUS,
                true
        );

        if (player != null) {
            this.patrolCenter = player.position();
        }
    }

    private void selectNextPoint() {
        double angle =
                this.phantom.getRandom().nextDouble()
                        * Math.PI * 2.0D;

        double radius =
                PATROL_RADIUS_MIN
                        + this.phantom.getRandom().nextDouble()
                        * (PATROL_RADIUS_MAX - PATROL_RADIUS_MIN);

        double height =
                PATROL_HEIGHT_MIN
                        + this.phantom.getRandom().nextDouble()
                        * (PATROL_HEIGHT_MAX - PATROL_HEIGHT_MIN);

        Vec3 target = new Vec3(
                this.patrolCenter.x + Math.cos(angle) * radius,
                this.patrolCenter.y + height,
                this.patrolCenter.z + Math.sin(angle) * radius
        );

        ((PhantomAccessor) this.phantom)
                .ahr$setMoveTargetPoint(target);
    }

    private net.minecraft.core.BlockPos getBombingTarget() {
        return ((PhantomAHRAccess) this.phantom)
                .ahr$getBombingTarget();
    }
}