package item;

import main.AHRMain;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

public class AHRNetherKnowledgeItem extends Item {

    private static final Identifier NETHER_KNOWLEDGE =
            Identifier.fromNamespaceAndPath(AHRMain.MOD_ID, "nether_knowledge");

    private static final int USE_DURATION = 100;
    private static final double RADIUS = 16.0;

    public AHRNetherKnowledgeItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity user) {
        return USE_DURATION;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack itemStack) {
        return ItemUseAnimation.BLOCK;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int ticksRemaining) {
        super.onUseTick(level, livingEntity, itemStack, ticksRemaining);

        if (level.isClientSide()) {
            int elapsed = USE_DURATION - ticksRemaining;

            int interval;
            if (elapsed < 25) {
                interval = 10;
            } else if (elapsed < 50) {
                interval = 7;
            } else if (elapsed < 75) {
                interval = 4;
            } else {
                interval = 2;
            }

            if (elapsed % interval == 0) {
                double radius = 0.5 + (elapsed / (double) USE_DURATION) * 0.8;

                level.addParticle(
                        ParticleTypes.ENCHANT,
                        livingEntity.getX() + (level.getRandom().nextDouble() - 0.5) * radius,
                        livingEntity.getY() + 1.0 + level.getRandom().nextDouble() * 0.8,
                        livingEntity.getZ() + (level.getRandom().nextDouble() - 0.5) * radius,
                        0.0,
                        0.02,
                        0.0
                );

                level.playSound(
                        null,
                        livingEntity.blockPosition(),
                        SoundEvents.ENCHANTMENT_TABLE_USE,
                        SoundSource.PLAYERS,
                        0.5F,
                        1.0F
                );
            }
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.BOOK_PUT,
                player.getSoundSource(),
                1.0F,
                1.0F
        );

        player.getCooldowns().addCooldown(player.getItemInHand(hand), USE_DURATION);
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entity) {
        if (level instanceof ServerLevel serverLevel && entity instanceof ServerPlayer player) {
            AdvancementHolder advancement = serverLevel.getServer()
                    .getAdvancements()
                    .get(NETHER_KNOWLEDGE);

            if (advancement != null) {
                serverLevel.getPlayers(candidate ->
                        candidate.distanceToSqr(player) <= RADIUS * RADIUS
                ).forEach(candidate -> {
                    candidate.getAdvancements().award(advancement, "unlock");
                });
            }

            level.playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.BOOK_PAGE_TURN,
                    player.getSoundSource(),
                    1.0F,
                    1.0F
            );
        }

        return itemStack;
    }
}
