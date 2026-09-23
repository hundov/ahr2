package mixin;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Unique
    private static final float SPRINT_EXHAUSTION = 0.25F; // default 0.1
    @Unique
    private static final float SHIFT_EXHAUSTION = 0.005F; // default 0
    @Unique
    private static final float WALK_EXHAUSTION = 0.01F; // default 0

    @Unique
    private static final float SWIM_EXHAUSTION = 0.03F; // default 0.01
    @Unique
    private static final float UNDERWATER_EXHAUSTION = 0.02F; // default 0.01
    @Unique
    private static final float WATER_WALK_EXHAUSTION = 0.02F; // default 0.01

    @Unique
    private static final float JUMP_EXHAUSTION = 0.1F; // default 0.05
    @Unique
    private static final float SPRINT_JUMP_EXHAUSTION = 0.5F; // default 0.2

    @Inject(
            method = "restoreFrom",
            at = @At("TAIL")
    )
    private void preserveFoodData(ServerPlayer oldPlayer, boolean restoreAll, CallbackInfo ci) {
        if (!restoreAll) {
            ServerPlayer player = (ServerPlayer) (Object) this;

            player.getFoodData().setFoodLevel(
                    oldPlayer.getFoodData().getFoodLevel()
            );

            player.getFoodData().setSaturation(
                    oldPlayer.getFoodData().getSaturationLevel()
            );
        }
    }

    @Inject(
            method = "startSleepInBed",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventSleepWithNegativeEffects(BlockPos pos, CallbackInfoReturnable<Either<Player.BedSleepingProblem, Unit>> cir) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        boolean hasNegativeEffect = player.getActiveEffects().stream()
                .anyMatch(effect -> effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL);

        if (hasNegativeEffect) {
            player.sendOverlayMessage(
                    Component.translatable("ahr2.sleep.negative_effect")
            );
            cir.setReturnValue(
                    Either.left(Player.BedSleepingProblem.OTHER_PROBLEM)
            );
        }
    }

    // swimming
    @Inject(
            method = "checkMovementStatistics",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V", ordinal = 0),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void modifySwimmingExhaustion(double dx, double dy, double dz, CallbackInfo ci, int distance) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        player.causeFoodExhaustion(SWIM_EXHAUSTION * (float) distance * 0.01F);
        ci.cancel(); // Отменяем ванильный вызов causeFoodExhaustion, который шел следом
    }

    // underwater
    @Inject(
            method = "checkMovementStatistics",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V", ordinal = 1),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void modifyUnderwaterExhaustion(double dx, double dy, double dz, CallbackInfo ci, int distance) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        player.causeFoodExhaustion(UNDERWATER_EXHAUSTION * (float) distance * 0.01F);
        ci.cancel();
    }

    // in water
    @Inject(
            method = "checkMovementStatistics",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V", ordinal = 2),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void modifyWaterWalkExhaustion(double dx, double dy, double dz, CallbackInfo ci, int horizontalDistance) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        player.causeFoodExhaustion(WATER_WALK_EXHAUSTION * (float) horizontalDistance * 0.01F);
        ci.cancel();
    }

    // 4. sprint
    @Inject(
            method = "checkMovementStatistics",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V", ordinal = 3),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void modifySprintExhaustion(double dx, double dy, double dz, CallbackInfo ci, int horizontalDistance) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        player.causeFoodExhaustion(SPRINT_EXHAUSTION * (float) horizontalDistance * 0.01F);
        ci.cancel();
    }

    // shift
    @Inject(
            method = "checkMovementStatistics",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V", ordinal = 4),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void modifyCrouchExhaustion(double dx, double dy, double dz, CallbackInfo ci, int horizontalDistance) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        player.causeFoodExhaustion(SHIFT_EXHAUSTION * (float) horizontalDistance * 0.01F);
        ci.cancel();
    }

    // walk
    @Inject(
            method = "checkMovementStatistics",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V", ordinal = 5),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void modifyWalkExhaustion(double dx, double dy, double dz, CallbackInfo ci, int horizontalDistance) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        player.causeFoodExhaustion(WALK_EXHAUSTION * (float) horizontalDistance * 0.01F);
        ci.cancel();
    }

    @ModifyConstant(
            method = "jumpFromGround",
            constant = @Constant(floatValue = 0.2F)
    )
    private float modifySprintJumpExhaustion(float original) {
        return SPRINT_JUMP_EXHAUSTION;
    }

    @ModifyConstant(
            method = "jumpFromGround",
            constant = @Constant(floatValue = 0.05F)
    )
    private float modifyJumpExhaustion(float original) {
        return JUMP_EXHAUSTION;
    }

}