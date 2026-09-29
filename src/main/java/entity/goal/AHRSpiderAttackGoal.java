package entity.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import registry.AHRBlocks;

public class AHRSpiderAttackGoal extends MeleeAttackGoal {

    private static final float WEB_SPAWN_CHANCE = 0.30F;
    private static final int WEB_COOLDOWN_TICKS = 80;
    private static final double WEB_MAX_DISTANCE_SQR = 9.0D;

    private final Spider spider;

    private int webCooldownTicks;

    public AHRSpiderAttackGoal(Spider spider) {
        super(spider, 1.0D, true);

        this.spider = spider;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && !this.spider.isVehicle();
    }

    @Override
    public boolean canContinueToUse() {
        float brightness =
                this.spider.getLightLevelDependentMagicValue();

        if (brightness >= 0.5F
                && this.spider.getRandom().nextInt(100) == 0) {
            this.spider.setTarget(null);
            return false;
        }

        return super.canContinueToUse();
    }

    @Override
    public void tick() {
        if (this.webCooldownTicks > 0) {
            --this.webCooldownTicks;
        }

        super.tick();
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target) {
        if (!this.canPerformAttack(target)) {
            return;
        }

        this.resetAttackCooldown();
        this.spider.swing(
                net.minecraft.world.InteractionHand.MAIN_HAND
        );

        if (!this.spider.doHurtTarget(
                (ServerLevel) this.spider.level(),
                target
        )) {
            return;
        }

        this.tryPlaceWeb(target);
    }

    private void tryPlaceWeb(LivingEntity target) {
        if (this.webCooldownTicks > 0) {
            return;
        }

        if (this.spider.getRandom().nextFloat() >= WEB_SPAWN_CHANCE) {
            return;
        }

        if (this.spider.distanceToSqr(target)
                > WEB_MAX_DISTANCE_SQR) {
            return;
        }

        Level level = this.spider.level();

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockPos targetPos = target.blockPosition();
        BlockState state = serverLevel.getBlockState(targetPos);

        if (!canReplace(state)) {
            return;
        }

        if (serverLevel.setBlock(
                targetPos,
                AHRBlocks.SPIDER_WEB.defaultBlockState(),
                Block.UPDATE_ALL
        )) {
            this.webCooldownTicks = WEB_COOLDOWN_TICKS;
        }
    }

    private static boolean canReplace(BlockState state) {
        Block block = state.getBlock();

        return state.isAir()
                || state.is(BlockTags.FLOWERS)
                || block == Blocks.SHORT_GRASS
                || block == Blocks.TALL_GRASS
                || block == Blocks.FERN
                || block == Blocks.LARGE_FERN
                || state.is(BlockTags.CROPS);
    }
}