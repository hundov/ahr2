package mixin;

import entity.AHRCreeper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityType.Builder.class)
public abstract class EntityTypeMixin<T extends Entity> {

    @Shadow
    @Final
    @Mutable
    private EntityType.EntityFactory<T> factory;

    @SuppressWarnings("unchecked")
    @Inject(
            method = "build",
            at = @At("HEAD")
    )
    private void replaceCreeperFactory(
            ResourceKey<EntityType<?>> name,
            CallbackInfoReturnable<EntityType<T>> cir
    ) {
        if (name.identifier().toString().equals("minecraft:creeper")) {
            this.factory = (type, level) ->
                    (T) new AHRCreeper(
                            (EntityType<? extends Creeper>) type,
                            level
                    );
        }
    }
}