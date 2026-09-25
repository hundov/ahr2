package registry;

import main.AHRMain;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class AHREffects {

    private static final int INSOMNIA_COLOR = 0x4B3A5A;

    public static final Holder<MobEffect> INSOMNIA = Registry.registerForHolder(
            BuiltInRegistries.MOB_EFFECT,
            Identifier.fromNamespaceAndPath(AHRMain.MOD_ID, "insomnia"),
            new MobEffect(MobEffectCategory.HARMFUL, INSOMNIA_COLOR) {}
    );

    private AHREffects() {}

    public static void init() {}

}
