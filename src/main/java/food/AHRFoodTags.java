package food;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class AHRFoodTags {

    public static final TagKey<Item> RAW_FOOD =
            TagKey.create(
                    Registries.ITEM,
                    Identifier.fromNamespaceAndPath("ahr2", "raw_food")
            );

    public static final TagKey<Item> PERISHABLE_ITEM =
            TagKey.create(
                    Registries.ITEM,
                    Identifier.fromNamespaceAndPath("ahr2", "perishable")
            );

    private AHRFoodTags() {}
}
