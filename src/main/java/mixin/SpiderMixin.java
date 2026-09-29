package mixin;

import entity.goal.AHRSpiderAttackGoal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.spider.Spider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Spider.class)
public abstract class SpiderMixin {

    @Inject(
            method = "registerGoals",
            at = @At("TAIL")
    )
    private void replaceAttackGoal(CallbackInfo ci) {
        Spider spider = (Spider) (Object) this;
        MobAccessor accessor = (MobAccessor) spider;

        accessor.getGoalSelector().getAvailableGoals().removeIf(
                wrappedGoal ->
                        wrappedGoal.getGoal() instanceof MeleeAttackGoal
        );

        accessor.getGoalSelector().addGoal(
                4,
                new AHRSpiderAttackGoal(spider)
        );
    }
}