package mixin;

import entity.PhantomAHRAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

@Mixin(Entity.class)
public abstract class EntityGameEventListenerMixin {

    @Inject(
            method = "updateDynamicGameEventListener",
            at = @At("HEAD")
    )
    private void ahr$registerGameEventListener(
            BiConsumer<DynamicGameEventListener<?>, ServerLevel> action,
            CallbackInfo ci
    ) {
        Entity entity = (Entity) (Object) this;

        if (entity instanceof PhantomAHRAccess phantom
                && entity.level() instanceof ServerLevel) {

            action.accept(
                    phantom.ahr$getDynamicGameEventListener(),
                    (ServerLevel) entity.level()
            );
        }
    }
}