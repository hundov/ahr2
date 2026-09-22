package food;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Map;

public class AHRShelfLife {

    private static final int DEFAULT_SHELF_LIFE = 7;

    private static final Map<Item, Integer> SHELF_LIVES = Map.<Item, Integer>ofEntries(

            // Crops / ingredients
            Map.entry(Items.WHEAT, 30),
            Map.entry(Items.HAY_BLOCK, 30),

            Map.entry(Items.APPLE, 5),
            Map.entry(Items.GOLDEN_APPLE, 15),
            Map.entry(Items.ENCHANTED_GOLDEN_APPLE, 60),

            Map.entry(Items.CARROT, 7),
            Map.entry(Items.GOLDEN_CARROT, 14),

            Map.entry(Items.POTATO, 14),
            Map.entry(Items.POISONOUS_POTATO, 7),

            Map.entry(Items.BEETROOT, 7),

            Map.entry(Items.MELON, 7),
            Map.entry(Items.MELON_SLICE, 3),

            Map.entry(Items.PUMPKIN, 14),
            Map.entry(Items.CARVED_PUMPKIN, 7),

            Map.entry(Items.SWEET_BERRIES, 3),
            Map.entry(Items.GLOW_BERRIES, 3),

            Map.entry(Items.CHORUS_FRUIT, 7),

            // Animal products
            Map.entry(Items.EGG, 7),
            Map.entry(Items.BLUE_EGG, 7),
            Map.entry(Items.BROWN_EGG, 7),
            Map.entry(Items.MILK_BUCKET, 3),

            // Raw meat
            Map.entry(Items.BEEF, 2),
            Map.entry(Items.PORKCHOP, 2),
            Map.entry(Items.CHICKEN, 2),
            Map.entry(Items.MUTTON, 2),
            Map.entry(Items.RABBIT, 2),

            // Raw fish
            Map.entry(Items.COD, 1),
            Map.entry(Items.SALMON, 1),
            Map.entry(Items.TROPICAL_FISH, 1),
            Map.entry(Items.PUFFERFISH, 1),

            // Cooked meat
            Map.entry(Items.COOKED_BEEF, 4),
            Map.entry(Items.COOKED_PORKCHOP, 4),
            Map.entry(Items.COOKED_CHICKEN, 4),
            Map.entry(Items.COOKED_MUTTON, 4),
            Map.entry(Items.COOKED_RABBIT, 4),

            // Cooked fish
            Map.entry(Items.COOKED_COD, 3),
            Map.entry(Items.COOKED_SALMON, 3),

            // Bakery
            Map.entry(Items.BREAD, 5),
            Map.entry(Items.COOKIE, 7),
            Map.entry(Items.CAKE, 3),
            Map.entry(Items.PUMPKIN_PIE, 5),

            // Soups / stews
            Map.entry(Items.BEETROOT_SOUP, 2),
            Map.entry(Items.MUSHROOM_STEW, 2),
            Map.entry(Items.RABBIT_STEW, 2),
            Map.entry(Items.SUSPICIOUS_STEW, 1),

            // Mushrooms / plants
            Map.entry(Items.BROWN_MUSHROOM, 3),
            Map.entry(Items.RED_MUSHROOM, 3),

            Map.entry(Items.KELP, 3),
            Map.entry(Items.DRIED_KELP, 30),
            Map.entry(Items.DRIED_KELP_BLOCK, 30),

            // Ingredients
            Map.entry(Items.SUGAR, 60),
            Map.entry(Items.COCOA_BEANS, 30),

            // Honey
            Map.entry(Items.HONEY_BOTTLE, 300),
            Map.entry(Items.HONEY_BLOCK, 300),

            // Undesirable food
            Map.entry(Items.ROTTEN_FLESH, 3),
            Map.entry(Items.SPIDER_EYE, 3)
    );

    public static int get(Item item) {
        return SHELF_LIVES.getOrDefault(item, DEFAULT_SHELF_LIFE);
    }

    private AHRShelfLife() {}

}