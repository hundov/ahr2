package player;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public record AHRFoodHistory(List<Item> foods) {

    public static final Codec<AHRFoodHistory> CODEC =
            BuiltInRegistries.ITEM.byNameCodec()
                    .listOf()
                    .xmap(AHRFoodHistory::new, AHRFoodHistory::foods);

    public static final StreamCodec<RegistryFriendlyByteBuf, AHRFoodHistory> STREAM_CODEC =
            ByteBufCodecs.registry(Registries.ITEM)
                    .apply(ByteBufCodecs.list())
                    .map(
                            AHRFoodHistory::new,
                            AHRFoodHistory::foods
                    );

    public AHRFoodHistory {
        foods = List.copyOf(foods);
    }

    public AHRFoodHistory() {
        this(List.of());
    }

    public AHRFoodHistory add(Item item, int maxSize) {
        List<Item> updated = new ArrayList<>(foods);

        updated.add(item);

        if (updated.size() > maxSize) {
            updated.removeFirst();
        }

        return new AHRFoodHistory(updated);
    }

    public static AHRFoodHistory get(Player player) {
        return player.getAttachedOrCreate(AHRAttachments.FOOD_HISTORY);
    }

    public int count(Item item) {
        int count = 0;

        for (Item food : foods) {
            if (food == item) count++;
        }

        return count;
    }

    public List<Item> foods() {
        return foods;
    }

}