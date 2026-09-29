package events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import player.AHRInventoryExhaustion;
import server.AHRServer;

public final class AHRServerEvents {

    public static void init() {
        ServerLevelEvents.LOAD.register(AHRServerEvents::onLevelLoad);
        ServerTickEvents.END_SERVER_TICK.register(AHRInventoryExhaustion::tick);
    }

    private static void onLevelLoad(MinecraftServer server, ServerLevel level) {
        AHRServer.onLevelLoad(server, level);
    }

    private AHRServerEvents() {
    }
}