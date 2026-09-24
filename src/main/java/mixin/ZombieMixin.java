package mixin;

import entity.goal.ZombieDestroyCropGoal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
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

        GoalSelector goalSelector = ((MobAccessor) (Object) zombie).getGoalSelector();

        goalSelector.addGoal(
                4,
                new ZombieDestroyCropGoal(zombie, 1.0, 3)
        );
    }

}
