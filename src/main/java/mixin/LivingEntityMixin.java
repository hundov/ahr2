package mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Phantom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Unique
    private static final float PHANTOM_EXPLOSION_RADIUS = 2.0F;

    @Inject(
            method = "hurtServer",
            at = @At("HEAD")
    )
    private void ahr$phantomExplodeOnDamage(
            ServerLevel level,
            DamageSource source,
            float amount,
            CallbackInfoReturnable<Boolean> cir
    ) {
        Entity entity = (Entity) (Object) this;

        if (!(entity instanceof Phantom phantom)) return;
        if (source.is(DamageTypeTags.IS_EXPLOSION)) return;
        if (!phantom.isAlive()) return;

        level.explode(
                phantom,
                phantom.getX(),
                phantom.getY(),
                phantom.getZ(),
                PHANTOM_EXPLOSION_RADIUS,
                false,
                net.minecraft.world.level.Level.ExplosionInteraction.BLOCK
        );

        phantom.discard();
    }
}