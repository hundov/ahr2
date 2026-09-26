package registry;

import main.AHRMain;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gameevent.GameEvent;

public final class AHRGameEvents {

    public static final Holder.Reference<GameEvent> ZOMBIE_STUCK =
            register("zombie_stuck", 48);

    public static final Holder.Reference<GameEvent> TARGET_UNREACHABLE =
            register("target_unreachable", 196);

    public static void init() {
    }

    private static Holder.Reference<GameEvent> register(
            String name,
            int notificationRadius
    ) {
        return Registry.registerForHolder(
                BuiltInRegistries.GAME_EVENT,
                Identifier.fromNamespaceAndPath(
                        AHRMain.MOD_ID,
                        name
                ),
                new GameEvent(notificationRadius)
        );
    }

    private AHRGameEvents() {
    }
}