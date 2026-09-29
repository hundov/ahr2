package registry;

import block.AHRWebBlock;
import main.AHRMain;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public final class AHRBlocks {

    public static final Block SPIDER_WEB = register(
            "spider_web",
            AHRWebBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.COBWEB)
    );

    public static void init() {
    }

    private static <T extends Block> T register(
            String name,
            Function<BlockBehaviour.Properties, T> blockFactory,
            BlockBehaviour.Properties properties
    ) {
        ResourceKey<Block> key = ResourceKey.create(
                net.minecraft.core.registries.Registries.BLOCK,
                Identifier.fromNamespaceAndPath(AHRMain.MOD_ID, name)
        );

        return Registry.register(
                net.minecraft.core.registries.BuiltInRegistries.BLOCK,
                key,
                blockFactory.apply(properties.setId(key))
        );
    }

    private AHRBlocks() {
    }
}