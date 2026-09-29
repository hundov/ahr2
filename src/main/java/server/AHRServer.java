package server;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gamerules.GameRules;

public final class AHRServer {

    public static void onLevelLoad(MinecraftServer server, ServerLevel level) {
        level.getGameRules().set(GameRules.LOCATOR_BAR, false, server);
    }

    private AHRServer() {
    }
}