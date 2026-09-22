package mixin;

import block.AHRCropBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.function.Function;

@Mixin(Blocks.class)
public class BlocksMixin {


    @ModifyArgs(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/Blocks;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;"
            )
    )
    private static void replaceWheatFactory(Args args) {
        String id = args.get(0);

        switch(id) {
            case "wheat" -> args.set(
                    1,
                    (Function<BlockBehaviour.Properties, Block>) properties ->
                            new AHRCropBlock(
                                    properties,
                                    7,
                                    AHRCropBlock.ShapeType.WHEAT
                            )
            );

            case "carrots" -> args.set(
                    1,
                    (Function<BlockBehaviour.Properties, Block>) properties ->
                            new AHRCropBlock(
                                    properties,
                                    7,
                                    AHRCropBlock.ShapeType.CARROT
                            )
            );

            case "potatoes" -> args.set(
                    1,
                    (Function<BlockBehaviour.Properties, Block>) properties ->
                            new AHRCropBlock(
                                    properties,
                                    7,
                                    AHRCropBlock.ShapeType.POTATO
                            )
            );

            case "beetroots" -> args.set(
                    1,
                    (Function<BlockBehaviour.Properties, Block>) properties ->
                            new AHRCropBlock(
                                    properties,
                                    3,
                                    AHRCropBlock.ShapeType.BEETROOT
                            )
            );
        }
    }
}