package player;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import registry.AHRAttachments;
import registry.AHREffects;

public final class AHRPlayerSleep {

    public static final long MIN_SLEEP_DURATION_TICKS = 100L;

    private static final int INSOMNIA_MIN_DURATION_TICKS = 2 * 60 * 20;
    private static final int INSOMNIA_MAX_DURATION_TICKS = 10 * 60 * 20;

    private static final float INSOMNIA_CHANCE_AFTER_SLEEP = 0.02F;
    private static final float INSOMNIA_CHANCE_AFTER_FAILED_SLEEP = 0.01F;

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

        if (player.getRandom().nextFloat() < insomniaChance) {
            int duration = INSOMNIA_MIN_DURATION_TICKS
                    + player.getRandom().nextInt(
                    INSOMNIA_MAX_DURATION_TICKS
                            - INSOMNIA_MIN_DURATION_TICKS
                            + 1
            );

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

    public static void handleWakeUp(
            ServerPlayer player,
            long sleepDuration
    ) {
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

    private AHRPlayerSleep() {
    }
}