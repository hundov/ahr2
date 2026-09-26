package main.client.mixin;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(OptionInstance.class)
public class OptionInstanceButtonMixin {

    @Inject(
            method = "createButton*",
            at = @At("RETURN")
    )
    private void disableGammaButton(
            Options options,
            int x,
            int y,
            int width,
            Consumer<?> onValueChanged,
            CallbackInfoReturnable<AbstractWidget> cir
    ) {
        if ((Object) this == options.gamma()) {
            cir.getReturnValue().active = false;
        }
    }
}