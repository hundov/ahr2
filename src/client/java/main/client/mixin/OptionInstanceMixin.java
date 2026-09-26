package main.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionInstance.class)
public class OptionInstanceMixin {

    @Inject(
            method = "set",
            at = @At("HEAD"),
            cancellable = true
    )
    private void lockGamma(
            Object value,
            CallbackInfo ci
    ) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.options != null
                && (Object) this == minecraft.options.gamma()) {
            ci.cancel();
        }
    }
}