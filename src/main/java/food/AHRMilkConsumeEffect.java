package food;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.Level;
import registry.AHRConsumeEffects;

public record AHRMilkConsumeEffect() implements ConsumeEffect {

    private static final float NAUSEA_DURATION_MULTIPLIER = 0.90F;

    public static final AHRMilkConsumeEffect INSTANCE =
            new AHRMilkConsumeEffect();

    public static final MapCodec<AHRMilkConsumeEffect> CODEC =
            MapCodec.unit(INSTANCE);

    public static final StreamCodec<RegistryFriendlyByteBuf, AHRMilkConsumeEffect> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<AHRMilkConsumeEffect> getType() {
        return AHRConsumeEffects.MILK;
    }

    @Override
    public boolean apply(
            Level level,
            ItemStack stack,
            LivingEntity user
    ) {
        MobEffectInstance nausea = user.getEffect(MobEffects.NAUSEA);

        if (nausea == null) {
            return false;
        }

        MobEffectInstance reducedNausea =
                nausea.withScaledDuration(NAUSEA_DURATION_MULTIPLIER);

        user.forceAddEffect(reducedNausea, null);

        return true;
    }
}