package mixin;

import food.AHRMilkConsumeEffect;
import item.AHRBowlItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.function.Function;

@Mixin(Items.class)
public class ItemsMixin {

    // injection in registerItem(final String name)
    @ModifyArgs(
            method = "registerItem(Ljava/lang/String;)Lnet/minecraft/world/item/Item;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/Items;registerItem(Lnet/minecraft/resources/ResourceKey;Ljava/util/function/Function;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;"
            )
    )
    private static void replaceFactoryString(Args args) {
        ResourceKey<Item> key = args.get(0);

        if ("bowl".equals(key.identifier().getPath())) {

            args.set(
                    1,
                    (Function<Item.Properties, Item>) AHRBowlItem::new
            );
        }
    }

    // injection in registerItem(final String name, final Item.Properties properties)
    @ModifyArgs(
            method = "registerItem(Ljava/lang/String;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/Items;registerItem(Lnet/minecraft/resources/ResourceKey;Ljava/util/function/Function;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;"
            )
    )
    private static void replaceFactoryStringAndProperties(Args args) {
        ResourceKey<Item> key = args.get(0);

        if ("milk_bucket".equals(key.identifier().getPath())) {
            args.set(
                    2,
                    ((Item.Properties) args.get(2)).component(
                            DataComponents.CONSUMABLE,
                            Consumables.defaultDrink()
                                    .onConsume(AHRMilkConsumeEffect.INSTANCE)
                                    .build()
                    )
            );
        }
    }
}
