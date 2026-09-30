package food;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;

/**
 * Handles food-related negative and positive effects applied to players.
 * <p>
 * Responsible for:
 * - effects from raw food;
 * - effects from food with zero nutrition;
 * - effects from spoiled food;
 * - common effect duration and amplifier randomization.
 * <p>
 * Does not determine when food is spoiled or how food values are calculated.
*/
public final class AHRFoodEffects {

    // Chance for each effect from raw food.
    private static final float RAW_NAUSEA_CHANCE = 0.70F;
    private static final float RAW_HUNGER_CHANCE = 0.70F;
    private static final float RAW_POISON_CHANCE = 0.50F;

    // Default duration range for random food effects.
    private static final int EFFECT_MIN_DURATION_SECONDS = 15;
    private static final int EFFECT_MAX_DURATION_SECONDS = 180;

    // Random amplifier distribution:
    // 0 = 80%, 1 = 15%, 2 = 5%.
    private static final int AMPLIFIER_BASE_CHANCE = 80;
    private static final int AMPLIFIER_SECOND_CHANCE = 95;

    /**
     * Applies a random negative effect when food provides no nutrition.
     */
    public static void applyZeroNutritionEffect(ServerPlayer player, Level level) {
        RandomSource random = level.getRandom();

        Holder<MobEffect> effect = switch (random.nextInt(4)) {
            case 0 -> MobEffects.SLOWNESS;
            case 1 -> MobEffects.NAUSEA;
            case 2 -> MobEffects.HUNGER;
            default -> MobEffects.POISON;
        };

        player.addEffect(createRandomEffect(effect, random));
    }

    /**
     * Applies the negative effects associated with eating raw food.
     * Each effect is rolled independently.
     */
    public static void applyRawFoodEffects(ServerPlayer player, Level level) {
        RandomSource random = level.getRandom();

        if (random.nextFloat() < RAW_NAUSEA_CHANCE) {
            player.addEffect(createRandomEffect(MobEffects.NAUSEA, random));
        }

        if (random.nextFloat() < RAW_HUNGER_CHANCE) {
            player.addEffect(createRandomEffect(MobEffects.HUNGER, random));
        }

        if (random.nextFloat() < RAW_POISON_CHANCE) {
            player.addEffect(createRandomEffect(MobEffects.POISON, random));
        }
    }

    /**
     * Applies effects caused by eating spoiled food.
     * The severity increases with the food's age relative to its shelf life.
     */
    public static void applySpoiledFoodEffects(ServerPlayer player, Level level, int age, int shelfLife) {
        RandomSource random = level.getRandom();

        // Severely spoiled food.
        if (age >= shelfLife * 4) {
            player.addEffect(createEffect(MobEffects.POISON, random, 30, 120, random.nextInt(3)));
            player.addEffect(createEffect(MobEffects.HUNGER, random, 180, 360, 2));
            return;
        }

        // Moderately spoiled food.
        if (age >= shelfLife * 2) {
            player.addEffect(createEffect(MobEffects.NAUSEA, random, 60, 180, 0));

            if (random.nextFloat() < 0.5F) {
                player.addEffect(createEffect(MobEffects.HUNGER, random, 60, 180, 1));
            }

            return;
        }

        // Recently spoiled food.
        player.addEffect(createEffect(MobEffects.NAUSEA, random, 30, 120, 0));
    }

    /**
     * Creates an effect with a random duration and a fixed amplifier.
     */
    private static MobEffectInstance createEffect(Holder<MobEffect> effect, RandomSource random, int minDurationSeconds, int maxDurationSeconds, int amplifier) {
        int durationSeconds = random.nextIntBetweenInclusive(minDurationSeconds, maxDurationSeconds);

        return new MobEffectInstance(effect, durationSeconds * 20, amplifier);
    }

    /**
     * Creates a food effect with a random duration and amplifier.
     */
    private static MobEffectInstance createRandomEffect(Holder<MobEffect> effect, RandomSource random) {
        int amplifierRoll = random.nextInt(100);

        int amplifier;

        if (amplifierRoll < AMPLIFIER_BASE_CHANCE) amplifier = 0;
        else if (amplifierRoll < AMPLIFIER_SECOND_CHANCE) amplifier = 1;
        else amplifier = 2;

        return createEffect(effect, random, EFFECT_MIN_DURATION_SECONDS, EFFECT_MAX_DURATION_SECONDS, amplifier);
    }

    private AHRFoodEffects() {
    }
}