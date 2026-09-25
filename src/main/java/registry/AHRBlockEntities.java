package registry;

import block.AHRCropBlockEntity;
import food.cake.CakeBlockEntity;
import main.AHRMain;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class AHRBlockEntities {

    public static final BlockEntityType<AHRCropBlockEntity> CROP = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(AHRMain.MOD_ID, "crop"),
            FabricBlockEntityTypeBuilder.create(
                    AHRCropBlockEntity::new,
                    Blocks.WHEAT,
                    Blocks.CARROTS,
                    Blocks.POTATOES,
                    Blocks.BEETROOTS
            ).build()
    );

    public static final BlockEntityType<CakeBlockEntity> CAKE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(AHRMain.MOD_ID, "cake"),
            FabricBlockEntityTypeBuilder.create(
                    CakeBlockEntity::new,
                    Blocks.CAKE
            ).build()
    );


    private AHRBlockEntities() {}

    public static void init() {}

}
