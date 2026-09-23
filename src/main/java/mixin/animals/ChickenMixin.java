package mixin.animals;

import mixin.MobAccessor;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chicken.class)
public class ChickenMixin {

    @Inject(
            method = "registerGoals",
            at = @At("TAIL")
    )
    private void addPlayerAvoidance(CallbackInfo ci) {
        Chicken chicken = (Chicken) (Object) this;
        GoalSelector goalSelector = ((MobAccessor) (Object) chicken).getGoalSelector();

        goalSelector.addGoal(
                2,
                new AvoidEntityGoal<>(
                        chicken,
                        Player.class,
                        15.0F,
                        1.0D,
                        1.25D
                )
        );
    }

}
