import component.AHRComponents;
import events.AHRPlayerEvents;
import loot.AHRLoot;
import net.fabricmc.api.ModInitializer;
import network.AHRNetworking;
import registry.AHRBlockEntities;

public class main implements ModInitializer {

    @Override
    public void onInitialize() {
        AHRComponents.init();
        AHRLoot.init();
        AHRNetworking.init();
        AHRPlayerEvents.init();
        AHRBlockEntities.init();
    }
}
