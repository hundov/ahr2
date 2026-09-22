package player;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import util.Logger;

public class AHRInventoryExhaustion {

    private static final int INTERVAL_TICKS = 200;
    private static final float EXHAUSTION_PER_SLOT = 0.005F;

    private static int tickCounter = 0;

    private static final Logger LOG = new Logger();
    static {
        LOG.enabled = false;
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter++;

            if (tickCounter < INTERVAL_TICKS) return;
            tickCounter = 0;

            for (Player player : server.getPlayerList().getPlayers()) {
                if (player.gameMode() != GameType.SURVIVAL
                        && player.gameMode() != GameType.ADVENTURE) {
                    continue;
                }
                int occupiedSlots = 0;

                for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
                    if (!stack.isEmpty()) occupiedSlots++;
                }

                if (occupiedSlots > 0) {
                    float exhaustion = occupiedSlots * EXHAUSTION_PER_SLOT;
                    player.causeFoodExhaustion(exhaustion);
                    LOG.send("exhaustion per slots in inventory: " + occupiedSlots + " [sum: " + exhaustion + "]");
                }

            }
        });
    }

    private AHRInventoryExhaustion() {}
}
