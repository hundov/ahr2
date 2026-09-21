package mixin;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Invoker("didNotMove")
    protected abstract boolean invokeDidNotMove(double dx, double dy, double dz);

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

    /**
     * @author hundov
     * @reason Allows AHR to independently balance exhaustion costs for different movement types.
     */
    @Overwrite
    public void checkMovementStatistics(
            final double dx,
            final double dy,
            final double dz
    ) {
        ServerPlayer player = (ServerPlayer) (Object) this;

        if (!player.isPassenger() && !invokeDidNotMove(dx, dy, dz)) {
            if (player.isSwimming()) {
                int distance = Math.round(
                        (float) Math.sqrt(dx * dx + dy * dy + dz * dz) * 100.0F
                );

                if (distance > 0) {
                    player.awardStat(Stats.SWIM_ONE_CM, distance);
                    player.causeFoodExhaustion(
                            SWIM_EXHAUSTION * distance * 0.01F
                    );
                }
            } else if (player.isEyeInFluid(FluidTags.WATER)) {
                int distance = Math.round(
                        (float) Math.sqrt(dx * dx + dy * dy + dz * dz) * 100.0F
                );

                if (distance > 0) {
                    player.awardStat(
                            Stats.WALK_UNDER_WATER_ONE_CM,
                            distance
                    );

                    player.causeFoodExhaustion(
                            UNDERWATER_EXHAUSTION * distance * 0.01F
                    );
                }
            } else if (player.isInWater()) {
                int horizontalDistance = Math.round(
                        (float) Math.sqrt(dx * dx + dz * dz) * 100.0F
                );

                if (horizontalDistance > 0) {
                    player.awardStat(
                            Stats.WALK_ON_WATER_ONE_CM,
                            horizontalDistance
                    );

                    player.causeFoodExhaustion(
                            WATER_WALK_EXHAUSTION * horizontalDistance * 0.01F
                    );
                }
            } else if (player.onClimbable()) {
                if (dy > 0.0F) {
                    player.awardStat(
                            Stats.CLIMB_ONE_CM,
                            (int) Math.round(dy * 100.0F)
                    );
                }
            } else if (player.onGround()) {
                int horizontalDistance = Math.round(
                        (float) Math.sqrt(dx * dx + dz * dz) * 100.0F
                );

                if (horizontalDistance > 0) {
                    if (player.isSprinting()) {
                        player.awardStat(
                                Stats.SPRINT_ONE_CM,
                                horizontalDistance
                        );

                        player.causeFoodExhaustion(
                                SPRINT_EXHAUSTION * horizontalDistance * 0.01F
                        );
                    } else if (player.isCrouching()) {
                        player.awardStat(
                                Stats.CROUCH_ONE_CM,
                                horizontalDistance
                        );

                        player.causeFoodExhaustion(
                                SHIFT_EXHAUSTION * horizontalDistance * 0.01F
                        );
                    } else {
                        player.awardStat(
                                Stats.WALK_ONE_CM,
                                horizontalDistance
                        );

                        player.causeFoodExhaustion(
                                WALK_EXHAUSTION * horizontalDistance * 0.01F
                        );
                    }
                }
            } else if (player.isFallFlying()) {
                int distance = Math.round(
                        (float) Math.sqrt(dx * dx + dy * dy + dz * dz) * 100.0F
                );

                player.awardStat(
                        Stats.AVIATE_ONE_CM,
                        distance
                );
            } else {
                int horizontalDistance = Math.round(
                        (float) Math.sqrt(dx * dx + dz * dz) * 100.0F
                );

                if (horizontalDistance > 25) {
                    player.awardStat(
                            Stats.FLY_ONE_CM,
                            horizontalDistance
                    );
                }
            }
        }
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
