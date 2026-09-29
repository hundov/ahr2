package player;

import net.minecraft.server.level.ServerPlayer;

public final class AHRPlayerExhaustion {

    public static final float SPRINT_EXHAUSTION = 0.25F;
    public static final float SHIFT_EXHAUSTION = 0.005F;
    public static final float WALK_EXHAUSTION = 0.01F;

    public static final float SWIM_EXHAUSTION = 0.03F;
    public static final float UNDERWATER_EXHAUSTION = 0.02F;
    public static final float WATER_WALK_EXHAUSTION = 0.02F;

    public static final float JUMP_EXHAUSTION = 0.1F;
    public static final float SPRINT_JUMP_EXHAUSTION = 0.5F;

    public static void applySwimming(ServerPlayer player, int distance) {
        player.causeFoodExhaustion(
                SWIM_EXHAUSTION * (float) distance * 0.01F
        );
    }

    public static void applyUnderwater(ServerPlayer player, int distance) {
        player.causeFoodExhaustion(
                UNDERWATER_EXHAUSTION * (float) distance * 0.01F
        );
    }

    public static void applyWaterWalk(ServerPlayer player, int distance) {
        player.causeFoodExhaustion(
                WATER_WALK_EXHAUSTION * (float) distance * 0.01F
        );
    }

    public static void applySprint(ServerPlayer player, int distance) {
        player.causeFoodExhaustion(
                SPRINT_EXHAUSTION * (float) distance * 0.01F
        );
    }

    public static void applyShift(ServerPlayer player, int distance) {
        player.causeFoodExhaustion(
                SHIFT_EXHAUSTION * (float) distance * 0.01F
        );
    }

    public static void applyWalk(ServerPlayer player, int distance) {
        player.causeFoodExhaustion(
                WALK_EXHAUSTION * (float) distance * 0.01F
        );
    }

    private AHRPlayerExhaustion() {
    }
}