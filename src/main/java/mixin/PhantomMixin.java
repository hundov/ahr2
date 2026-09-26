package mixin;

import entity.PhantomAHRAccess;
import entity.goal.PhantomBombingGoal;
import entity.goal.PhantomPatrolGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import registry.AHRGameEvents;

@Mixin(Phantom.class)
public abstract class PhantomMixin
        implements VibrationSystem, PhantomAHRAccess {

    @Unique
    private static final int AHR_VIBRATION_LISTENER_RADIUS = 128;

    @Unique
    private final VibrationSystem.Data ahr$vibrationData =
            new VibrationSystem.Data();

    @Unique
    private final DynamicGameEventListener<VibrationSystem.Listener>
            ahr$dynamicGameEventListener =
            new DynamicGameEventListener<>(
                    new VibrationSystem.Listener(this)
            );

    @Unique
    private final VibrationSystem.User ahr$vibrationUser =
            new AHRVibrationUser();

    @Unique
    private @Nullable BlockPos ahr$bombingTarget;

    @Unique
    private boolean ahr$goalsRegistered;

    @Redirect(
            method = "registerGoals",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V"
            )
    )
    private void ahr$replacePhantomGoals(
            GoalSelector goalSelector,
            int priority,
            Goal goal
    ) {
        if (!this.ahr$goalsRegistered) {
            Phantom phantom = (Phantom) (Object) this;

            goalSelector.addGoal(
                    1,
                    new PhantomBombingGoal(phantom)
            );

            goalSelector.addGoal(
                    2,
                    new PhantomPatrolGoal(phantom)
            );

            this.ahr$goalsRegistered = true;
        }
    }

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void ahr$tickVibrations(CallbackInfo ci) {
        if (((Entity) (Object) this).level()
                instanceof ServerLevel serverLevel) {

            VibrationSystem.Ticker.tick(
                    serverLevel,
                    this.ahr$vibrationData,
                    this.ahr$vibrationUser
            );
        }
    }

    @Override
    public DynamicGameEventListener<?> ahr$getDynamicGameEventListener() {
        return this.ahr$dynamicGameEventListener;
    }

    @Override
    public VibrationSystem.Data getVibrationData() {
        return this.ahr$vibrationData;
    }

    @Override
    public VibrationSystem.User getVibrationUser() {
        return this.ahr$vibrationUser;
    }

    @Override
    public @Nullable BlockPos ahr$getBombingTarget() {
        return this.ahr$bombingTarget;
    }

    @Override
    public void ahr$setBombingTarget(BlockPos target) {
        this.ahr$bombingTarget = target.immutable();
    }

    @Override
    public void ahr$clearBombingTarget() {
        this.ahr$bombingTarget = null;
    }

    @Unique
    private class AHRVibrationUser implements VibrationSystem.User {

        private final PositionSource positionSource =
                new EntityPositionSource(
                        (Entity) (Object) PhantomMixin.this,
                        ((Phantom) (Object) PhantomMixin.this)
                                .getEyeHeight()
                );

        @Override
        public int getListenerRadius() {
            return AHR_VIBRATION_LISTENER_RADIUS;
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
            return event.is(AHRGameEvents.TARGET_UNREACHABLE);
        }

        @Override
        public boolean canReceiveVibration(
                ServerLevel level,
                BlockPos pos,
                Holder<GameEvent> event,
                GameEvent.Context context
        ) {
            Phantom phantom =
                    (Phantom) (Object) PhantomMixin.this;

            return phantom.isAlive()
                    && PhantomMixin.this.ahr$bombingTarget == null;
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
            PhantomMixin.this.ahr$setBombingTarget(pos);
        }
    }
}