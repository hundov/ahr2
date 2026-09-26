package main.client.mixin;

import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.stream.Collectors;

@Mixin(DebugScreenEntries.class)
public class DebugScreenEntriesMixin {

    @Inject(
            method = "allEntries",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void hideRestrictedEntries(
            CallbackInfoReturnable<Map<Identifier, DebugScreenEntry>> cir
    ) {
        Map<Identifier, DebugScreenEntry> filteredEntries =
                cir.getReturnValue().entrySet().stream()
                        .filter(entry -> !isRestricted(entry.getKey()))
                        .collect(Collectors.toUnmodifiableMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue
                        ));

        cir.setReturnValue(filteredEntries);
    }

    @Unique
    private static boolean isRestricted(Identifier id) {
        String path = id.getPath();

        return path.startsWith("looking_")
                || path.equals("player_position")
                || path.equals("player_section_position")
                || path.equals("biome")
                || path.equals("local_difficulty")
                || path.equals("day_count")
                || path.equals("heightmap")
                || path.equals("light_levels");
    }
}