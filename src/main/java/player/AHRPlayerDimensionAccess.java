package player;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public final class AHRPlayerDimensionAccess {

    private static final Identifier NETHER_KNOWLEDGE =
            Identifier.fromNamespaceAndPath(
                    "ahr2",
                    "nether_knowledge"
            );

    private static final Identifier END_KNOWLEDGE =
            Identifier.fromNamespaceAndPath(
                    "ahr2",
                    "end_knowledge"
            );

    public static boolean canEnter(
            ServerPlayer player,
            ServerLevel level
    ) {
        if (level.dimension() == Level.NETHER) {
            return hasAdvancement(player, NETHER_KNOWLEDGE);
        }

        if (level.dimension() == Level.END) {
            return hasAdvancement(player, END_KNOWLEDGE);
        }

        return true;
    }

    private static boolean hasAdvancement(
            ServerPlayer player,
            Identifier id
    ) {
        AdvancementHolder advancement =
                player.level()
                        .getServer()
                        .getAdvancements()
                        .get(id);

        if (advancement == null) {
            return false;
        }

        return player.getAdvancements()
                .getOrStartProgress(advancement)
                .isDone();
    }

    private AHRPlayerDimensionAccess() {
    }
}