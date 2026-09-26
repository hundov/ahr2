package main.client.mixin;

import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public class OptionsMixin {

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void forceMinimumGamma(
            CallbackInfo ci
    ) {
        Options options = (Options) (Object) this;

        options.gamma().set(0.0D);
    }
}