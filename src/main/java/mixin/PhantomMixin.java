package mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Phantom.class)
public abstract class PhantomMixin {

    @Unique
    private static final float EXPLOSION_RADIUS = 2.0F;

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    private void ahr$explodeOnCollision(CallbackInfo ci) {
        Phantom phantom = (Phantom) (Object) this;

        if (!phantom.horizontalCollision && !phantom.verticalCollision) return;
        if (!(phantom.level() instanceof ServerLevel level)) return;
        if (!phantom.isAlive()) return;

        level.explode(
                phantom,
                phantom.getX(),
                phantom.getY(),
                phantom.getZ(),
                EXPLOSION_RADIUS,
                false,
                Level.ExplosionInteraction.BLOCK
        );

        phantom.discard();
    }
}