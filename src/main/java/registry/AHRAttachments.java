package registry;

import com.mojang.serialization.Codec;
import main.AHRMain;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import food.AHRFoodHistory;

public class AHRAttachments {

    public static final AttachmentType<AHRFoodHistory> FOOD_HISTORY =
            AttachmentRegistry.create(
                    Identifier.fromNamespaceAndPath(AHRMain.MOD_ID, "food_history"),
                    builder -> builder.initializer(AHRFoodHistory::new)
                            .persistent(AHRFoodHistory.CODEC)
                            .copyOnDeath()
                            .syncWith(
                                    AHRFoodHistory.STREAM_CODEC,
                                    AttachmentSyncPredicate.targetOnly()
                            )
            );

    public static final AttachmentType<Float> INSOMNIA_CHANCE =
            AttachmentRegistry.create(
                    Identifier.fromNamespaceAndPath(AHRMain.MOD_ID, "insomnia_chance"),
                    builder -> builder.initializer(() -> 0.01F)
                            .persistent(Codec.FLOAT)
                            .copyOnDeath()
                            .syncWith(
                                    ByteBufCodecs.FLOAT,
                                    AttachmentSyncPredicate.targetOnly()
                            )
            );

    private AHRAttachments() {}

}
