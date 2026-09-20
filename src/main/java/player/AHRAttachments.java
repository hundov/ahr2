package player;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;

public class AHRAttachments {

    public static final AttachmentType<AHRFoodHistory> FOOD_HISTORY =
            AttachmentRegistry.create(
                    Identifier.fromNamespaceAndPath("ahr2", "food_history"),
                    builder -> builder.initializer(AHRFoodHistory::new).persistent(AHRFoodHistory.CODEC).copyOnDeath()
            );

    private AHRAttachments() {}

}
