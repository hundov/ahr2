import net.fabricmc.api.ModInitializer;
import network.AHRNetworking;
import registry.AHRBlockEntities;

public class main implements ModInitializer {

    @Override
    public void onInitialize() {
        AHRNetworking.init();
        AHRBlockEntities.init();
    }
}
