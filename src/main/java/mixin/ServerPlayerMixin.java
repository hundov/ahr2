package mixin;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @Inject(
            method = "restoreFrom",
            at = @At("TAIL")
    )
    private void preserveFoodData(ServerPlayer oldPlayer, boolean restoreAll, CallbackInfo ci) {
        if (!restoreAll) {
            ServerPlayer player = (ServerPlayer) (Object) this;

            player.getFoodData().setFoodLevel(
                    oldPlayer.getFoodData().getFoodLevel()
            );

            player.getFoodData().setSaturation(
                    oldPlayer.getFoodData().getSaturationLevel()
            );
        }
    }

    @Inject(
            method = "startSleepInBed",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventSleepWithNegativeEffects(BlockPos pos, CallbackInfoReturnable<Either<Player.BedSleepingProblem, Unit>> cir) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        boolean hasNegativeEffect = player.getActiveEffects().stream()
                .anyMatch(effect -> effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL);

        if (hasNegativeEffect) {
            player.sendOverlayMessage(
                    Component.translatable("ahr2.sleep.negative_effect")
            );
            cir.setReturnValue(
                    Either.left(Player.BedSleepingProblem.OTHER_PROBLEM)
            );
        }
    }

}
