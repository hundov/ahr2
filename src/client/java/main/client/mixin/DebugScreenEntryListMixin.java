package main.client.mixin;

import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DebugScreenEntryList.class)
public class DebugScreenEntryListMixin {

    @Shadow
    private List<Identifier> currentlyEnabled;

    @Inject(
            method = "rebuildCurrentList",
            at = @At("TAIL")
    )
    private void removeRestrictedEntries(CallbackInfo ci) {
        this.currentlyEnabled.removeIf(DebugScreenEntryListMixin::isRestricted);
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