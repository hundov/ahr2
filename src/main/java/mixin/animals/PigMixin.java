package mixin.animals;

import mixin.MobAccessor;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.cow.AbstractCow;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Pig.class)
public class PigMixin {

    @Inject(
            method = "registerGoals",
            at = @At("TAIL")
    )
    private void addPlayerAvoidance(CallbackInfo ci) {
        Pig pig = (Pig) (Object) this;
        GoalSelector goalSelector = ((MobAccessor) (Object) pig).getGoalSelector();

        goalSelector.addGoal(
                2,
                new AvoidEntityGoal<>(
                        pig,
                        Player.class,
                        15.0F,
                        1.0D,
                        1.25D
                )
        );
    }

}
