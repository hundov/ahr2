package events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gamerules.GameRules;

public class AHRServerEvents {

    public static void init() {
        ServerLevelEvents.LOAD.register(AHRServerEvents::onLevelLoad);
    }

    private static void onLevelLoad(MinecraftServer server, ServerLevel level) {
        level.getGameRules().set(GameRules.LOCATOR_BAR, false, server);
    }

    private AHRServerEvents() {}

}
