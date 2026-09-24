package mixin;

import entity.goal.ZombieDestroyCropGoal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
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

        goalSelector.addGoal(
                4,
                new ZombieDestroyCropGoal(zombie, 1.0D, 3)
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
