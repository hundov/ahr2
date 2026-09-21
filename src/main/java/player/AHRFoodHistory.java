package player;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public record AHRFoodHistory(List<Item> foods) {

    public static final int EASY_SIZE = 24;
    public static final int NORMAL_SIZE = 36;
    public static final int HARD_SIZE = 48;

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

    public AHRFoodHistory add(Item item, Level level) {
        List<Item> updated = new ArrayList<>(foods);

        updated.add(item);
        int maxSize = getMaxSize(level);

        if (updated.size() > maxSize) {
            updated.removeFirst();
        }

        return new AHRFoodHistory(updated);
    }

    public static int getMaxSize(Level level) {
        return switch (level.getDifficulty()) {
            case NORMAL -> NORMAL_SIZE;
            case HARD -> HARD_SIZE;
            default -> EASY_SIZE;
        };
    }

    public AHRFoodHistory updateSize(Level level) {
        int maxSize = getMaxSize(level);

        if (foods.size() <= maxSize) return this;
        return new AHRFoodHistory(foods.subList(foods.size() - maxSize, foods.size()));
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