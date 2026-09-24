package mixin.effects;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.PoisonMobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(PoisonMobEffect.class)
public class PoisonMobEffectMixin {

    /**
     * Allows the Poison effect to reduce an entity's health below 1 HP.
     *
     * @author hundov
     * @reason AHR makes Poison lethal as part of its hardcore food survival mechanics.
     */
    @Overwrite
    public boolean applyEffectTick(ServerLevel level, LivingEntity mob, int amplifier) {
        mob.hurtServer(level, mob.damageSources().magic(), 1.0F);
        return true;
    }
}
