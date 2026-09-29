package events;

import food.AHRFoodHistory;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.level.ServerPlayer;

public final class AHRPlayerEvents {

    public static void init() {
        ServerPlayerEvents.JOIN.register(AHRPlayerEvents::onPlayerJoin);
    }

    private static void onPlayerJoin(ServerPlayer player) {
        AHRFoodHistory.onPlayerJoin(player);
    }

    private AHRPlayerEvents() {
    }
}