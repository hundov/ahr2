package mixin;

import item.AHRBowlItem;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.function.Function;

@Mixin(Items.class)
public class ItemsMixin {

    @ModifyArgs(
            method = "registerItem(Ljava/lang/String;)Lnet/minecraft/world/item/Item;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/Items;registerItem(Lnet/minecraft/resources/ResourceKey;Ljava/util/function/Function;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;"
            )
    )
    private static void replaceBowlFactory(Args args) {
        ResourceKey<Item> key = args.get(0);

        if ("bowl".equals(key.identifier().getPath())) {

            args.set(
                    1,
                    (Function<Item.Properties, Item>) AHRBowlItem::new
            );
        }
    }
}
