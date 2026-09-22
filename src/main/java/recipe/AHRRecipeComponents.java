package recipe;

import component.AHRComponents;
import food.AHRFoodTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import util.Logger;

public class AHRRecipeComponents {

    private static final Logger LOG = new Logger();
    static {
        LOG.enabled = false;
    }

    public static ItemStack applyMadeOn(
            ItemStack result,
            RecipeInput input
    ) {
        if (result.isEmpty() || !result.is(AHRFoodTags.PERISHABLE_ITEM)) {
            return result;
        }

        Integer oldestMadeOn = null;

        for (int i = 0; i < input.size(); i++) {
            ItemStack ingredient = input.getItem(i);
            Integer madeOn = ingredient.get(AHRComponents.MADE_ON);

            if (madeOn == null) {
                continue;
            }

            if (oldestMadeOn == null || madeOn < oldestMadeOn) {
                oldestMadeOn = madeOn;
            }
        }

        if (oldestMadeOn == null) {
            System.err.println(
                    "[AHR] ERROR: Perishable recipe result has no MADE_ON: "
                            + result.getItem()
            );
            return result;
        }

        result.set(AHRComponents.MADE_ON, oldestMadeOn);
        LOG.send("MADE_ON: " + result.get(AHRComponents.MADE_ON));
        return result;
    }

    private AHRRecipeComponents() {}
}