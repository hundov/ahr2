package player;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ConsumableListener;
import net.minecraft.world.level.Level;

public class AHRFoodHistoryListener implements ConsumableListener {

    @Override
    public void onConsume(Level level, LivingEntity user, ItemStack stack, Consumable consumable) {
        if (!(user instanceof Player player)) return;
        AHRFoodHistory history = player.getAttachedOrCreate(AHRAttachments.FOOD_HISTORY);
        player.setAttached(AHRAttachments.FOOD_HISTORY, history.add(stack.getItem(), 36));
    }
}
