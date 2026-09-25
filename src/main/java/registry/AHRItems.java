package registry;

import item.AHRBowlWithWaterItem;
import main.AHRMain;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.Function;

public class AHRItems {

    public static final Item BOWL_WITH_WATER = register(
            create("bowl_with_water"),
            AHRBowlWithWaterItem::new,
            new Item.Properties()
                    .stacksTo(1)
    );

    public static final Item BOWL_WITH_EGG = register(
            create("bowl_with_egg"),
            Item::new,
            new Item.Properties()
                    .stacksTo(1)
                    .craftRemainder(Items.BOWL)
    );

    public static final Item BOWL_WITH_EGG_IN_WATER = register(
            create("bowl_with_egg_in_water"),
            Item::new,
            new Item.Properties()
                    .stacksTo(1)
                    .craftRemainder(BOWL_WITH_WATER)
    );

    public static final Item BOWL_WITH_BOILED_EGG = register(
            create("bowl_with_boiled_egg"),
            Item::new,
            new Item.Properties()
                    .stacksTo(1)
                    .food(
                            new FoodProperties.Builder()
                                    .nutrition(6)
                                    .saturationModifier(0.6F)
                                    .build()
                    )
                    .usingConvertsTo(Items.BOWL)
    );

    public static final Item BOWL_WITH_FRIED_EGG = register(
            create("bowl_with_fried_egg"),
            Item::new,
            new Item.Properties()
                    .stacksTo(1)
                    .food(
                            new FoodProperties.Builder()
                                    .nutrition(6)
                                    .saturationModifier(0.6F)
                                    .build()
                    )
                    .usingConvertsTo(Items.BOWL)
    );

    private static ResourceKey<Item> create(String name) {
        return ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                Identifier.fromNamespaceAndPath(AHRMain.MOD_ID, name)
        );
    }

    private static Item register(ResourceKey<Item> key, Function<Item.Properties, Item> factory, Item.Properties properties) {
        Item item = factory.apply(properties.setId(key));
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    public static void init() {}
    private AHRItems() {}

}
