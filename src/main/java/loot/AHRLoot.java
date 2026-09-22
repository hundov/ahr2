package loot;

import component.AHRComponents;
import food.AHRFoodTags;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;

public class AHRLoot {

    private static final int MAX_CHEST_AGE = 24;

    public static void init() {
        LootTableEvents.MODIFY_DROPS.register((holder, context, drops) -> {
            ServerLevel level = context.getLevel();

            boolean chestLoot = isChestLoot(holder);

            int currentDay = Math.toIntExact(level.getOverworldClockTime() / 24000L);

            for (ItemStack stack : drops) {
                if (!stack.is(AHRFoodTags.PERISHABLE_ITEM)) continue;
                if (stack.has(AHRComponents.MADE_ON)) continue;

                if (chestLoot) {
                    int randomAge =
                            level.getRandom().nextIntBetweenInclusive(0, MAX_CHEST_AGE);

                    stack.set(
                            AHRComponents.MADE_ON,
                            currentDay - randomAge
                    );
                } else {
                    stack.set(
                            AHRComponents.MADE_ON,
                            currentDay
                    );
                }

                System.out.println(
                        "[AHR] MADE_ON: "
                                + stack.getItem()
                                + " -> "
                                + stack.get(AHRComponents.MADE_ON)
                );
            }
        });
    }

    private static boolean isChestLoot(Holder<LootTable> holder) {
        return holder.unwrapKey()
                .map(key -> {
                    Identifier id = key.identifier();

                    return id.getNamespace().equals("minecraft")
                            && id.getPath().startsWith("chests/");
                })
                .orElse(false);
    }

    private AHRLoot() {}
}