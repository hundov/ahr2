package mixin;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Unit;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.portal.TeleportTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import player.AHRPlayerDimensionAccess;
import player.AHRPlayerExhaustion;
import player.AHRPlayerExperience;
import player.AHRPlayerSleep;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    // ------------------------------------------- experience block

    @Unique
    private double experienceRemainder;

    // ------------------------------------------- insomnia block

    @Unique
    private long sleepStartTime;

    @Unique
    private boolean wasSleeping;

    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void preserveFoodData(ServerPlayer oldPlayer, boolean restoreAll, CallbackInfo ci) {
        if (!restoreAll) {
            ServerPlayer player = (ServerPlayer) (Object) this;

            player.getFoodData().setFoodLevel(oldPlayer.getFoodData().getFoodLevel());

            player.getFoodData().setSaturation(oldPlayer.getFoodData().getSaturationLevel());
        }
    }

    @Inject(method = "teleport*", at = @At("HEAD"), cancellable = true)
    private void ahr$checkDimensionAccess(TeleportTransition transition, CallbackInfoReturnable<ServerPlayer> cir) {
        ServerPlayer player = (ServerPlayer) (Object) this;

        if (AHRPlayerDimensionAccess.canEnter(player, transition.newLevel())) return;

        player.sendOverlayMessage(Component.translatable("ahr2.teleport"));

        cir.setReturnValue(null);
    }

    // ---------------------------------------------
    // Experience System
    // ---------------------------------------------

    @ModifyVariable(method = "giveExperiencePoints", at = @At("HEAD"), argsOnly = true, name = "i")
    private int modifyExperienceGain(int amount) {
        AHRPlayerExperience.Result result = AHRPlayerExperience.modify(amount, experienceRemainder);

        experienceRemainder = result.remainder();

        return result.experience();
    }

    // ---------------------------------------------
    // Sleep System
    // ---------------------------------------------

    @Inject(
            method = "startSleepInBed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;startSleepInBed(Lnet/minecraft/core/BlockPos;)Lcom/mojang/datafixers/util/Either;"
            ),
            cancellable = true
    )
    private void checkAhrSleep(BlockPos pos, CallbackInfoReturnable<Either<Player.BedSleepingProblem, Unit>> cir) {
        ServerPlayer player = (ServerPlayer) (Object) this;

        if (!AHRPlayerSleep.tryStartSleeping(player)) {
            cir.setReturnValue(Either.left(Player.BedSleepingProblem.OTHER_PROBLEM));
        }
    }

    @Inject(method = "startSleeping", at = @At("HEAD"))
    private void trackStartSleeping(BlockPos pos, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;

        sleepStartTime = player.level().getGameTime();
        wasSleeping = true;
    }

    @Inject(method = "stopSleepInBed", at = @At("HEAD"))
    private void increaseInsomniaChance(boolean forcefulWakeUp, boolean updateLevelList, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;

        if (!wasSleeping) return;

        long sleepDuration = player.level().getGameTime() - sleepStartTime;

        AHRPlayerSleep.handleWakeUp(player, sleepDuration);

        wasSleeping = false;
    }

    // ---------------------------------------------
    // Exhaustion System
    // ---------------------------------------------

    // Swimming
    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V", ordinal = 0), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void modifySwimmingExhaustion(double dx, double dy, double dz, CallbackInfo ci, int distance) {
        ServerPlayer player = (ServerPlayer) (Object) this;

        AHRPlayerExhaustion.applySwimming(player, distance);
        ci.cancel();
    }

    // Underwater
    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V", ordinal = 1), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void modifyUnderwaterExhaustion(double dx, double dy, double dz, CallbackInfo ci, int distance) {
        ServerPlayer player = (ServerPlayer) (Object) this;

        AHRPlayerExhaustion.applyUnderwater(player, distance);
        ci.cancel();
    }

    // Water Walk
    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V", ordinal = 2), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void modifyWaterWalkExhaustion(double dx, double dy, double dz, CallbackInfo ci, int horizontalDistance) {
        ServerPlayer player = (ServerPlayer) (Object) this;

        AHRPlayerExhaustion.applyWaterWalk(player, horizontalDistance);
        ci.cancel();
    }

    // Sprint
    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V", ordinal = 3), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void modifySprintExhaustion(double dx, double dy, double dz, CallbackInfo ci, int horizontalDistance) {
        ServerPlayer player = (ServerPlayer) (Object) this;

        AHRPlayerExhaustion.applySprint(player, horizontalDistance);
        ci.cancel();
    }

    // Shift
    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V", ordinal = 4), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void modifyCrouchExhaustion(double dx, double dy, double dz, CallbackInfo ci, int horizontalDistance) {
        ServerPlayer player = (ServerPlayer) (Object) this;

        AHRPlayerExhaustion.applyShift(player, horizontalDistance);
        ci.cancel();
    }

    // Walk
    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;causeFoodExhaustion(F)V", ordinal = 5), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void modifyWalkExhaustion(double dx, double dy, double dz, CallbackInfo ci, int horizontalDistance) {
        ServerPlayer player = (ServerPlayer) (Object) this;

        AHRPlayerExhaustion.applyWalk(player, horizontalDistance);
        ci.cancel();
    }

    @ModifyConstant(method = "jumpFromGround", constant = @Constant(floatValue = 0.2F))
    private float modifySprintJumpExhaustion(float original) {
        return AHRPlayerExhaustion.SPRINT_JUMP_EXHAUSTION;
    }

    @ModifyConstant(method = "jumpFromGround", constant = @Constant(floatValue = 0.05F))
    private float modifyJumpExhaustion(float original) {
        return AHRPlayerExhaustion.JUMP_EXHAUSTION;
    }

}