package events;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import registry.AHRAttachments;
import food.AHRFoodHistory;
import util.Logger;

public class AHRPlayerEvents {

    private static final Logger LOG = new Logger();
    static {
        LOG.enabled = true;
    }

    public static void init() {
        ServerPlayerEvents.JOIN.register(AHRPlayerEvents::onPlayerJoin);
    }

    private static void onPlayerJoin(ServerPlayer player) {
        AHRFoodHistory history = player.getAttachedOrCreate(AHRAttachments.FOOD_HISTORY);
        int maxSize = AHRFoodHistory.getMaxSize(player.level());

        {
            LOG.send(
                    "Food history: " +
                    "max=" + maxSize +
                    ", current=" + history.foods().size()
            );
        }

        AHRFoodHistory updated = history.updateSize(player.level());
        if (updated != history) {
            player.setAttached(AHRAttachments.FOOD_HISTORY, updated);

            {
                LOG.send(
                        "Food history updated: " +
                        "max=" + maxSize +
                        ", current=" + updated.foods().size()
                );
            }
        }
    }

    private AHRPlayerEvents() {}

}
