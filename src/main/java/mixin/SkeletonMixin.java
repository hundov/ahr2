package mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(AbstractSkeleton.class)
public abstract class SkeletonMixin {

    private static final double FOLLOW_RANGE = 36.0D;

    private static final double MOVEMENT_SPEED = 0.30D;

    private static final int ATTACK_INTERVAL_TICKS = 18;
    private static final int HARD_ATTACK_INTERVAL_TICKS = 10;

    private static final float EASY_SPREAD = 6.0F;
    private static final float NORMAL_SPREAD = 3.0F;
    private static final float HARD_SPREAD = 2.0F;

    @ModifyArgs(
            method = "performRangedAttack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/Projectile;spawnProjectileUsingShoot(Lnet/minecraft/world/entity/projectile/Projectile;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;DDDFF)Lnet/minecraft/world/entity/projectile/Projectile;"
            )
    )
    private void modifyArrowSpread(Args args) {
        if (!((Object) this instanceof Skeleton skeleton)) {
            return;
        }

        ServerLevel serverLevel = args.get(1);

        float spread = switch (serverLevel.getDifficulty()) {
            case HARD -> HARD_SPREAD;
            case NORMAL -> NORMAL_SPREAD;
            default -> EASY_SPREAD;
        };

        args.set(7, spread);
    }

    @Inject(method = "createAttributes", at = @At("RETURN"))
    private static void ahr2$modifyAttributes(
            CallbackInfoReturnable<AttributeSupplier.Builder> cir
    ) {
        cir.getReturnValue()
                .add(Attributes.FOLLOW_RANGE, FOLLOW_RANGE)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void modifyAttributes(CallbackInfo ci) {
        if (!((Object) this instanceof Skeleton skeleton)) {
            return;
        }

        AttributeInstance movementSpeed = skeleton.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) {
            movementSpeed.setBaseValue(MOVEMENT_SPEED);
        }

        AttributeInstance followRange = skeleton.getAttribute(Attributes.FOLLOW_RANGE);
        if (followRange != null) {
            followRange.setBaseValue(FOLLOW_RANGE);
        }
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void addAnimalTarget(CallbackInfo ci) {
        if (!((Object) this instanceof Skeleton skeleton)) {
            return;
        }

        MobAccessor accessor = (MobAccessor) skeleton;

        accessor.getTargetSelector().addGoal(
                3,
                new NearestAttackableTargetGoal<>(
                        skeleton,
                        Animal.class,
                        true
                )
        );
    }

    @Inject(
            method = "reassessWeaponGoal",
            at = @At("TAIL")
    )
    private void removeMeleeGoal(CallbackInfo ci) {
        if (!((Object) this instanceof Skeleton skeleton)) {
            return;
        }

        MobAccessor accessor = (MobAccessor) skeleton;

        accessor.getGoalSelector().getAvailableGoals().removeIf(
                wrappedGoal -> wrappedGoal.getGoal() instanceof MeleeAttackGoal
        );
    }

    @Inject(
            method = "getAttackInterval",
            at = @At("RETURN"),
            cancellable = true
    )
    private void modifyAttackInterval(
            CallbackInfoReturnable<Integer> cir
    ) {
        if ((Object) this instanceof Skeleton) {
            cir.setReturnValue(ATTACK_INTERVAL_TICKS);
        }
    }

    @Inject(
            method = "getHardAttackInterval",
            at = @At("RETURN"),
            cancellable = true
    )
    private void modifyHardAttackInterval(
            CallbackInfoReturnable<Integer> cir
    ) {
        if ((Object) this instanceof Skeleton) {
            cir.setReturnValue(HARD_ATTACK_INTERVAL_TICKS);
        }
    }
}