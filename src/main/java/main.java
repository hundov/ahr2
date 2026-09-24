import events.AHRPlayerEvents;
import net.fabricmc.api.ModInitializer;
import player.AHRInventoryExhaustion;
import registry.*;

public class main implements ModInitializer {

    @Override
    public void onInitialize() {

        AHRComponents.init();

        AHRLoot.init();
        AHRItems.init();

        AHRNetworking.init();

        AHRPlayerEvents.init();

        AHRBlockEntities.init();
        AHRInventoryExhaustion.init();
    }
}
