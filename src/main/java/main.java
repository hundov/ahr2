import registry.AHRComponents;
import events.AHRPlayerEvents;
import registry.AHRLoot;
import net.fabricmc.api.ModInitializer;
import registry.AHRNetworking;
import registry.AHRBlockEntities;
import registry.AHRItems;

public class main implements ModInitializer {

    @Override
    public void onInitialize() {
        AHRComponents.init();
        AHRLoot.init();
        AHRItems.init();
        AHRNetworking.init();
        AHRPlayerEvents.init();
        AHRBlockEntities.init();
    }
}
