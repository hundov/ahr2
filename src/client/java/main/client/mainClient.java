package main.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import network.AHRFoodHistoryPayload;
import registry.AHRAttachments;

public class mainClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        ClientPlayNetworking.registerGlobalReceiver(
                AHRFoodHistoryPayload.TYPE,
                (payload, context) -> {
                    if (context.client().player == null) return;

                    context.client().execute(() -> {
                        context.client().player.setAttached(
                                AHRAttachments.FOOD_HISTORY,
                                payload.history()
                        );
                    });
                }
        );
    }
}
