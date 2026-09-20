package network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import player.AHRFoodHistory;

public class AHRNetworking {

    public static void init() {
        PayloadTypeRegistry.clientboundPlay().register(
                AHRFoodHistoryPayload.TYPE,
                AHRFoodHistoryPayload.CODEC
        );
    }

    public static void syncFoodHistory(
            ServerPlayer player,
            AHRFoodHistory history
    ) {
        ServerPlayNetworking.send(
                player,
                new AHRFoodHistoryPayload(history)
        );
    }

    private AHRNetworking() {}

}
