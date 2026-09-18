import net.fabricmc.api.ModInitializer;
import registry.AHRBlockEntities;

public class main implements ModInitializer {

    @Override
    public void onInitialize() {
        AHRBlockEntities.init();
    }
}
