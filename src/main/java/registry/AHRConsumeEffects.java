package registry;

import food.effects.AHRHoneyBottleConsumeEffect;
import food.effects.AHRMilkConsumeEffect;
import main.AHRMain;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.consume_effects.ConsumeEffect;

public final class AHRConsumeEffects {

    public static final ConsumeEffect.Type<AHRMilkConsumeEffect> MILK =
            Registry.register(
                    BuiltInRegistries.CONSUME_EFFECT_TYPE,
                    Identifier.fromNamespaceAndPath(
                            AHRMain.MOD_ID,
                            "milk"
                    ),
                    new ConsumeEffect.Type<>(
                            AHRMilkConsumeEffect.CODEC,
                            AHRMilkConsumeEffect.STREAM_CODEC
                    )
            );

    public static final ConsumeEffect.Type<AHRHoneyBottleConsumeEffect> HONEY_BOTTLE =
            Registry.register(
                    BuiltInRegistries.CONSUME_EFFECT_TYPE,
                    Identifier.fromNamespaceAndPath(
                            AHRMain.MOD_ID,
                            "honey_bottle"
                    ),
                    new ConsumeEffect.Type<>(
                            AHRHoneyBottleConsumeEffect.CODEC,
                            AHRHoneyBottleConsumeEffect.STREAM_CODEC
                    )
            );

    private AHRConsumeEffects() {
    }

    public static void init() {
    }
}