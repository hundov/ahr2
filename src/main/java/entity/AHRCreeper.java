package entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.jspecify.annotations.Nullable;
import registry.AHRGameEvents;

import java.util.EnumSet;
import java.util.function.BiConsumer;

public class AHRCreeper extends Creeper implements VibrationSystem {

    private static final int VIBRATION_LISTENER_RADIUS = 48;

    private static final double VIBRATION_TARGET_REACH_DISTANCE = 2.0D;
    private static final double VIBRATION_MOVEMENT_SPEED = 1.0D;

    private static final int VIBRATION_PROGRESS_CHECK_INTERVAL_TICKS = 20;
    private static final int VIBRATION_TARGET_STUCK_TIMEOUT_TICKS = 200;
    private static final double VIBRATION_MIN_PROGRESS = 0.25D;

    private final DynamicGameEventListener<VibrationSystem.Listener> dynamicGameEventListener =
            new DynamicGameEventListener<>(new VibrationSystem.Listener(this));

    private final VibrationSystem.User vibrationUser = new VibrationUser();
    private final VibrationSystem.Data vibrationData = new VibrationSystem.Data();

    private @Nullable BlockPos vibrationTarget;

    private double vibrationTargetLastDistance;
    private int vibrationProgressCheckTimer;
    private int vibrationTargetStuckTime;

    public AHRCreeper(EntityType<? extends Creeper> type, Level level) {
        super(type, level);

        this.goalSelector.addGoal(
                2,
                new MoveToVibrationGoal(this)
        );
    }

    @Override
    public void tick() {
        if (this.level() instanceof ServerLevel serverLevel) {
            VibrationSystem.Ticker.tick(
                    serverLevel,
                    this.vibrationData,
                    this.vibrationUser
            );
        }

        super.tick();
    }

    @Override
    public void updateDynamicGameEventListener(
            BiConsumer<DynamicGameEventListener<?>, ServerLevel> action
    ) {
        if (this.level() instanceof ServerLevel serverLevel) {
            action.accept(
                    this.dynamicGameEventListener,
                    serverLevel
            );
        }
    }

    @Override
    public VibrationSystem.Data getVibrationData() {
        return this.vibrationData;
    }

    @Override
    public VibrationSystem.User getVibrationUser() {
        return this.vibrationUser;
    }

    private void setVibrationTarget(BlockPos pos) {
        this.vibrationTarget = pos.immutable();
        this.vibrationTargetLastDistance = Double.MAX_VALUE;
        this.vibrationProgressCheckTimer = 0;
        this.vibrationTargetStuckTime = 0;
    }

    private void clearVibrationTarget() {
        this.vibrationTarget = null;
        this.vibrationTargetLastDistance = Double.MAX_VALUE;
        this.vibrationProgressCheckTimer = 0;
        this.vibrationTargetStuckTime = 0;
    }

    private void emitTargetUnreachableEvent(BlockPos target) {
        this.level().gameEvent(
                this,
                AHRGameEvents.TARGET_UNREACHABLE,
                target
        );
    }

    private class VibrationUser implements VibrationSystem.User {

        private final PositionSource positionSource =
                new EntityPositionSource(
                        AHRCreeper.this,
                        AHRCreeper.this.getEyeHeight()
                );

        @Override
        public int getListenerRadius() {
            return VIBRATION_LISTENER_RADIUS;
        }

        @Override
        public PositionSource getPositionSource() {
            return this.positionSource;
        }

        @Override
        public TagKey<GameEvent> getListenableEvents() {
            return GameEventTags.VIBRATIONS;
        }

        @Override
        public boolean isValidVibration(
                Holder<GameEvent> event,
                GameEvent.Context context
        ) {
            return event.is(GameEvent.BLOCK_ACTIVATE)
                    || event.is(GameEvent.BLOCK_DEACTIVATE)
                    || event.is(GameEvent.BLOCK_OPEN)
                    || event.is(GameEvent.BLOCK_CLOSE)
                    || event.is(GameEvent.CONTAINER_OPEN)
                    || event.is(GameEvent.CONTAINER_CLOSE)
                    || event.is(AHRGameEvents.ZOMBIE_STUCK);
        }

        @Override
        public boolean canReceiveVibration(
                ServerLevel level,
                BlockPos pos,
                Holder<GameEvent> event,
                GameEvent.Context context
        ) {
            if (!AHRCreeper.this.isAlive()) {
                return false;
            }

            return AHRCreeper.this.getTarget() == null;
        }

        @Override
        public void onReceiveVibration(
                ServerLevel level,
                BlockPos pos,
                Holder<GameEvent> event,
                @Nullable Entity sourceEntity,
                @Nullable Entity projectileOwner,
                float receivingDistance
        ) {
            AHRCreeper.this.setVibrationTarget(pos);
        }
    }

    private static class MoveToVibrationGoal extends Goal {

        private final AHRCreeper creeper;

        private MoveToVibrationGoal(AHRCreeper creeper) {
            this.creeper = creeper;

            this.setFlags(EnumSet.of(
                    Flag.MOVE,
                    Flag.LOOK
            ));
        }

        @Override
        public boolean canUse() {
            return this.creeper.vibrationTarget != null
                    && this.creeper.getTarget() == null
                    && !this.creeper.isIgnited();
        }

        @Override
        public boolean canContinueToUse() {
            return this.creeper.vibrationTarget != null
                    && this.creeper.getTarget() == null
                    && !this.creeper.isIgnited();
        }

        @Override
        public void tick() {
            BlockPos target = this.creeper.vibrationTarget;

            if (target == null) {
                return;
            }

            double distance = Math.sqrt(
                    this.creeper.distanceToSqr(
                            target.getX() + 0.5D,
                            target.getY() + 0.5D,
                            target.getZ() + 0.5D
                    )
            );

            if (distance <= VIBRATION_TARGET_REACH_DISTANCE) {
                this.creeper.clearVibrationTarget();
                this.creeper.ignite();
                return;
            }

            this.updateProgress(distance);

            if (this.creeper.vibrationTargetStuckTime
                    >= VIBRATION_TARGET_STUCK_TIMEOUT_TICKS) {

                this.creeper.emitTargetUnreachableEvent(target);
                this.creeper.clearVibrationTarget();
                this.creeper.ignite();
                return;
            }

            this.creeper.getNavigation().moveTo(
                    target.getX() + 0.5D,
                    target.getY(),
                    target.getZ() + 0.5D,
                    VIBRATION_MOVEMENT_SPEED
            );

            this.creeper.getLookControl().setLookAt(
                    target.getX() + 0.5D,
                    target.getY() + 0.5D,
                    target.getZ() + 0.5D
            );
        }

        private void updateProgress(double currentDistance) {
            this.creeper.vibrationProgressCheckTimer++;

            if (this.creeper.vibrationProgressCheckTimer
                    < VIBRATION_PROGRESS_CHECK_INTERVAL_TICKS) {
                return;
            }

            this.creeper.vibrationProgressCheckTimer = 0;

            if (this.creeper.vibrationTargetLastDistance
                    == Double.MAX_VALUE) {

                this.creeper.vibrationTargetLastDistance = currentDistance;
                return;
            }

            double progress =
                    this.creeper.vibrationTargetLastDistance
                            - currentDistance;

            if (progress >= VIBRATION_MIN_PROGRESS) {
                this.creeper.vibrationTargetStuckTime = 0;
            } else {
                this.creeper.vibrationTargetStuckTime +=
                        VIBRATION_PROGRESS_CHECK_INTERVAL_TICKS;
            }

            this.creeper.vibrationTargetLastDistance = currentDistance;
        }

        @Override
        public void stop() {
            this.creeper.getNavigation().stop();
        }
    }
}