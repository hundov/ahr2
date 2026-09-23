package mixin.animals;

import mixin.MobAccessor;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.cow.AbstractCow;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractCow.class)
public class AbstractCowMixin {

    @Inject(
            method = "registerGoals",
            at = @At("TAIL")
    )
    private void addPlayerAvoidance(CallbackInfo ci) {
        AbstractCow cow = (AbstractCow) (Object) this;
        GoalSelector goalSelector = ((MobAccessor) (Object) cow).getGoalSelector();

        goalSelector.addGoal(
                2,
                new AvoidEntityGoal<>(
                        cow,
                        Player.class,
                        15.0F,
                        1.0D,
                        1.25D
                )
        );
    }

}
