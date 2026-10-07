package registry;

import item.AHRBowlWithWaterItem;
import item.AHREndKnowledgeItem;
import item.AHRNetherKnowledgeItem;
import main.AHRMain;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;

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

    public static final Item EMERALD_NUGGET = register(
            create("emerald_nugget"),
            Item::new,
            new Item.Properties()
    );

    public static final Item FOOD_DETECTOR = register(
            create("food_detector"),
            properties -> new BlockItem(AHRBlocks.FOOD_DETECTOR, properties),
            new Item.Properties()
                    .useBlockDescriptionPrefix()
    );

    public static final Item NETHER_KNOWLEDGE = register(
            create("nether_knowledge"),
            AHRNetherKnowledgeItem::new,
            new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)

    );

    public static final Item TRAVELER_SCRAPS = register(
            create("traveler_scraps"),
            Item::new,
            new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
    );

    public static final Item SCRAPS_OF_END = register(
            create("scraps_of_end"),
            Item::new,
            new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
    );

    public static final Item END_KNOWLEDGE = register(
            create("end_knowledge"),
            AHREndKnowledgeItem::new,
            new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)

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
