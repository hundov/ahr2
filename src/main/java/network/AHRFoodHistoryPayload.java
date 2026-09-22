package network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import food.AHRFoodHistory;

public record AHRFoodHistoryPayload(AHRFoodHistory history) implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath("ahr2", "food_history");

    public static final Type<AHRFoodHistoryPayload> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, AHRFoodHistoryPayload> CODEC =
            AHRFoodHistory.STREAM_CODEC.map(
                    AHRFoodHistoryPayload::new,
                    AHRFoodHistoryPayload::history
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
