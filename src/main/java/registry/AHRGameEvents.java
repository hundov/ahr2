package registry;

import main.AHRMain;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.gameevent.GameEvent;

public final class AHRGameEvents {

    public static final ResourceKey<GameEvent> ZOMBIE_STUCK_KEY =
            ResourceKey.create(
                    BuiltInRegistries.GAME_EVENT.key(),
                    Identifier.fromNamespaceAndPath(
                            AHRMain.MOD_ID,
                            "zombie_stuck"
                    )
            );

    public static final GameEvent ZOMBIE_STUCK =
            Registry.register(
                    BuiltInRegistries.GAME_EVENT,
                    ZOMBIE_STUCK_KEY,
                    new GameEvent(48)
            );

    public static void init() {
    }

    private AHRGameEvents() {
    }
}