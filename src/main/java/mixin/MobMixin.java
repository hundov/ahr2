package mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Phantom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin {

    @Inject(
            method = "isSunBurnTick",
            at = @At("HEAD"),
            cancellable = true
    )
    private void ahr$phantomNoSunBurn(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Phantom) cir.setReturnValue(false);
    }
}