package main.client.mixin;

import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {

    private static final String HARDCORE_MESSAGE_KEY = "ahr2.debug.hardcore_message";

    @ModifyArg(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;extractLines(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Ljava/util/List;Z)V",
                    ordinal = 0
            ),
            index = 1
    )
    private List<String> addHardcoreMessage(List<String> lines) {
        lines.add(Component.translatable(HARDCORE_MESSAGE_KEY).getString());
        return lines;
    }
}