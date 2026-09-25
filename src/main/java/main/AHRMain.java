package main;

import events.AHRPlayerEvents;
import net.fabricmc.api.ModInitializer;
import player.AHRInventoryExhaustion;
import registry.*;

public class AHRMain implements ModInitializer {

    public static final String MOD_ID = "ahr2";

    @Override
    public void onInitialize() {

        AHRComponents.init();

        AHRLoot.init();
        AHRItems.init();

        AHRGameEvents.init();

        AHREffects.init();
        AHRConsumeEffects.init();

        AHRNetworking.init();

        AHRPlayerEvents.init();

        AHRBlockEntities.init();
        AHRInventoryExhaustion.init();
    }
}
