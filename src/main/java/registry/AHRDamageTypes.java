package registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public class AHRDamageTypes {

    public static final ResourceKey<DamageType> SPOILED_FOOD =
            ResourceKey.create(
                    Registries.DAMAGE_TYPE,
                    Identifier.fromNamespaceAndPath("ahr2", "spoiled_food")
            );

    private AHRDamageTypes() {}
}
