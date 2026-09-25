package mixin;

import entity.goal.ZombieBreakObstacleGoal;
import entity.goal.ZombieDestroyCropGoal;
import entity.goal.ZombieUnreachableTargetGoal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.zombie.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Zombie.class)
public class ZombieMixin {

    @Inject(
            method = "registerGoals",
            at = @At("TAIL")
    )
    private void addAHRGoals(CallbackInfo ci) {
        Zombie zombie = (Zombie) (Object) this;

        MobAccessor accessor = (MobAccessor) (Object) zombie;

        GoalSelector goalSelector = accessor.getGoalSelector();
        GoalSelector targetSelector = accessor.getTargetSelector();

        ZombieDestroyCropGoal cropGoal =
                new ZombieDestroyCropGoal(
                        zombie,
                        1.0D,
                        3
                );

        ZombieBreakObstacleGoal obstacleGoal =
                new ZombieBreakObstacleGoal(zombie);

        goalSelector.addGoal(
                1,
                new AvoidEntityGoal<>(
                        zombie,
                        Creeper.class,
                        12.0F,
                        1.2D,
                        1.5D
                )
        );

        goalSelector.addGoal(
                3,
                obstacleGoal
        );

        goalSelector.addGoal(
                4,
                new ZombieUnreachableTargetGoal(
                        zombie,
                        cropGoal,
                        obstacleGoal
                )
        );

        goalSelector.addGoal(
                5,
                cropGoal
        );

        targetSelector.addGoal(
                3,
                new NearestAttackableTargetGoal<>(
                        zombie,
                        Animal.class,
                        true
                )
        );
    }
}