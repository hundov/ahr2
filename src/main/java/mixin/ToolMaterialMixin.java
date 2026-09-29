package mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import registry.AHRBlocks;

import java.util.Arrays;

@Mixin(ToolMaterial.class)
public abstract class ToolMaterialMixin {

    // whyy mojang?
    @ModifyArg(
            method = "applySwordProperties",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/HolderSet;direct([Lnet/minecraft/core/Holder;)Lnet/minecraft/core/HolderSet$Direct;"
            )
    )
    private Holder<Block>[] addSpiderWeb(
            Holder<Block>[] holders
    ) {
        Holder<Block>[] result = Arrays.copyOf(holders, holders.length + 1);

        result[holders.length] =
                AHRBlocks.SPIDER_WEB.builtInRegistryHolder();

        return result;
    }
}