package food;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import registry.AHRAttachments;

import java.util.ArrayList;
import java.util.List;

public record AHRFoodHistory(List<FoodEntry> foods) {

    public static final int EASY_SIZE = 24;
    public static final int NORMAL_SIZE = 36;
    public static final int HARD_SIZE = 48;

    public static final int FOOD_MEMORY_DAYS = 7;

    public static final Codec<FoodEntry> FOOD_ENTRY_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    BuiltInRegistries.ITEM.byNameCodec()
                            .fieldOf("item")
                            .forGetter(FoodEntry::item),
                    Codec.INT
                            .fieldOf("day")
                            .forGetter(FoodEntry::day)
            ).apply(instance, FoodEntry::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FoodEntry> FOOD_ENTRY_STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.registry(Registries.ITEM),
                    FoodEntry::item,
                    ByteBufCodecs.INT,
                    FoodEntry::day,
                    FoodEntry::new
            );

    public static final Codec<AHRFoodHistory> CODEC =
            FOOD_ENTRY_CODEC
                    .listOf()
                    .xmap(
                            AHRFoodHistory::new,
                            AHRFoodHistory::foods
                    );

    public static final StreamCodec<RegistryFriendlyByteBuf, AHRFoodHistory> STREAM_CODEC =
            FOOD_ENTRY_STREAM_CODEC
                    .apply(ByteBufCodecs.list())
                    .map(
                            AHRFoodHistory::new,
                            AHRFoodHistory::foods
                    );

    public AHRFoodHistory {foods = List.copyOf(foods);}
    public AHRFoodHistory() {this(List.of());}

    public AHRFoodHistory add(Item item, Level level) {
        List<FoodEntry> updated = new ArrayList<>(update(level).foods);

        updated.add(new FoodEntry(item, getCurrentDay(level)));

        int maxSize = getMaxSize(level);

        if (updated.size() > maxSize) updated.removeFirst();

        return new AHRFoodHistory(updated);
    }

    public static int getMaxSize(Level level) {
        return switch (level.getDifficulty()) {
            case NORMAL -> NORMAL_SIZE;
            case HARD -> HARD_SIZE;
            default -> EASY_SIZE;
        };
    }

    public AHRFoodHistory update(Level level) {
        int currentDay = getCurrentDay(level);

        List<FoodEntry> updated = foods.stream()
                .filter(entry ->
                        currentDay - entry.day() < FOOD_MEMORY_DAYS)
                .toList();

        if (updated.size() == foods.size()) return this;

        return new AHRFoodHistory(updated);
    }

    public AHRFoodHistory updateSize(Level level) {
        int maxSize = getMaxSize(level);

        if (foods.size() <= maxSize) return this;

        return new AHRFoodHistory(
                foods.subList(
                        foods.size() - maxSize,
                        foods.size())
        );
    }

    public int getEfficiency(Item item) {
        int previousCount = count(item);
        return Math.max(0, 100 - previousCount * 10);
    }

    public static AHRFoodHistory get(Player player) {
        AHRFoodHistory history = player.getAttachedOrCreate(AHRAttachments.FOOD_HISTORY);

        AHRFoodHistory updated = history.update(player.level());

        if (updated != history) {
            player.setAttached(AHRAttachments.FOOD_HISTORY, updated);
            return updated;
        }

        return history;
    }

    public static void onPlayerJoin(Player player) {
        AHRFoodHistory history = get(player);
        AHRFoodHistory updated = history.updateSize(player.level());

        if (updated != history) player.setAttached(AHRAttachments.FOOD_HISTORY, updated);
    }

    public int count(Item item) {
        int count = 0;

        for (FoodEntry food : foods) {
            if (food.item() == item) count++;
        }

        return count;
    }

    public List<FoodEntry> foods() {return foods;}
    private static int getCurrentDay(Level level) {return (int) (level.getOverworldClockTime() / 24000L);}
    public record FoodEntry(Item item, int day) { }
}