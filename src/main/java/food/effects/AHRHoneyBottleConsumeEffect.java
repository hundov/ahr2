package food.effects;

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

public record AHRHoneyBottleConsumeEffect() implements ConsumeEffect {

    private static final float POISON_DURATION_MULTIPLIER = 0.35F;

    public static final AHRHoneyBottleConsumeEffect INSTANCE =
            new AHRHoneyBottleConsumeEffect();

    public static final MapCodec<AHRHoneyBottleConsumeEffect> CODEC =
            MapCodec.unit(INSTANCE);

    public static final StreamCodec<RegistryFriendlyByteBuf, AHRHoneyBottleConsumeEffect> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<AHRHoneyBottleConsumeEffect> getType() {
        return AHRConsumeEffects.HONEY_BOTTLE;
    }

    @Override
    public boolean apply(
            Level level,
            ItemStack stack,
            LivingEntity user
    ) {
        MobEffectInstance nausea = user.getEffect(MobEffects.POISON);

        if (nausea == null) {
            return false;
        }

        MobEffectInstance reducedNausea =
                nausea.withScaledDuration(POISON_DURATION_MULTIPLIER);

        user.forceAddEffect(reducedNausea, null);

        return true;
    }
}