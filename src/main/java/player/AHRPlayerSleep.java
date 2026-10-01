package player;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.Level;
import registry.AHRAttachments;
import registry.AHREffects;

public final class AHRPlayerSleep {

    public static final long MIN_SLEEP_DURATION_TICKS = 100L;

    private static final int TICKS_PER_DAY = 24_000;

    private static final int INSOMNIA_DURATION_START_DAY = 1;
    private static final int INSOMNIA_DURATION_FULL_DAY = 10;

    private static final int INSOMNIA_MIN_DURATION_DAYS = 1;
    private static final int INSOMNIA_MAX_DURATION_DAYS = 4;
    private static final int INSOMNIA_ABSOLUTE_MAX_DURATION_DAYS = 5;

    private static final float INSOMNIA_CHANCE_AFTER_SLEEP = 0.02F;
    private static final float INSOMNIA_CHANCE_AFTER_FAILED_SLEEP = 0.01F;

    private static final float INSOMNIA_EASY_CHANCE_MULTIPLIER = 0.75F;
    private static final float INSOMNIA_NORMAL_CHANCE_MULTIPLIER = 1.0F;
    private static final float INSOMNIA_HARD_CHANCE_MULTIPLIER = 1.25F;

    public static boolean tryStartSleeping(ServerPlayer player) {
        boolean hasNegativeEffect = player.getActiveEffects().stream()
                .anyMatch(effect ->
                        effect.getEffect().value().getCategory()
                                == MobEffectCategory.HARMFUL
                );

        if (hasNegativeEffect) {
            player.sendOverlayMessage(
                    Component.translatable("ahr2.sleep.negative_effect")
            );

            return false;
        }

        float insomniaChance = player.getAttachedOrCreate(
                AHRAttachments.INSOMNIA_CHANCE
        );

        insomniaChance *= getInsomniaChanceMultiplier(player);

        if (player.getRandom().nextFloat() < insomniaChance) {
            int duration = getInsomniaDurationTicks(player);

            player.addEffect(
                    new MobEffectInstance(
                            AHREffects.INSOMNIA,
                            duration
                    )
            );

            player.sendOverlayMessage(
                    Component.translatable("ahr2.sleep.insomnia")
            );

            player.setAttached(
                    AHRAttachments.INSOMNIA_CHANCE,
                    INSOMNIA_CHANCE_AFTER_FAILED_SLEEP
            );

            return false;
        }

        return true;
    }

    public static void handleWakeUp(ServerPlayer player, long sleepDuration) {
        if (sleepDuration < MIN_SLEEP_DURATION_TICKS) {
            return;
        }

        float insomniaChance = player.getAttachedOrCreate(
                AHRAttachments.INSOMNIA_CHANCE
        );

        player.setAttached(
                AHRAttachments.INSOMNIA_CHANCE,
                insomniaChance + INSOMNIA_CHANCE_AFTER_SLEEP
        );
    }

    private static int getInsomniaDurationTicks(ServerPlayer player) {
        int currentDay = getCurrentDay(player.level());

        float progress = Mth.clamp(
                (currentDay - INSOMNIA_DURATION_START_DAY)
                        / (float) (
                        INSOMNIA_DURATION_FULL_DAY
                                - INSOMNIA_DURATION_START_DAY
                ),
                0.0F,
                1.0F
        );

        float maxDurationDays = Mth.lerp(
                progress,
                INSOMNIA_MIN_DURATION_DAYS,
                INSOMNIA_MAX_DURATION_DAYS
        );

        int maxDuration = Mth.clamp(
                (int) maxDurationDays,
                INSOMNIA_MIN_DURATION_DAYS,
                INSOMNIA_ABSOLUTE_MAX_DURATION_DAYS
        );

        int durationDays = INSOMNIA_MIN_DURATION_DAYS;

        if (maxDuration > INSOMNIA_MIN_DURATION_DAYS) {
            durationDays += player.getRandom().nextInt(
                    maxDuration - INSOMNIA_MIN_DURATION_DAYS + 1
            );
        }

        return durationDays * TICKS_PER_DAY;
    }

    private static float getInsomniaChanceMultiplier(ServerPlayer player) {
        return switch (player.level().getDifficulty()) {
            case PEACEFUL -> 0.0F;
            case EASY -> INSOMNIA_EASY_CHANCE_MULTIPLIER;
            case NORMAL -> INSOMNIA_NORMAL_CHANCE_MULTIPLIER;
            case HARD -> INSOMNIA_HARD_CHANCE_MULTIPLIER;
        };
    }

    private static int getCurrentDay(Level level) {
        return (int) (level.getOverworldClockTime() / TICKS_PER_DAY);
    }

    private AHRPlayerSleep() {
    }
}