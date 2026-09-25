package registry;

import com.mojang.serialization.Codec;
import main.AHRMain;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;

public class AHRComponents {

    public static final DataComponentType<Integer> MADE_ON =
            Registry.register(
                    BuiltInRegistries.DATA_COMPONENT_TYPE,
                    Identifier.fromNamespaceAndPath(AHRMain.MOD_ID, "made_on"),
                    DataComponentType.<Integer>builder()
                            .persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.VAR_INT)
                            .build()
            );

    public static void init() {}

    private AHRComponents() {}

}
