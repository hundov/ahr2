package item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Items;

public class AHRBowlWithWaterItem extends Item {

    public AHRBowlWithWaterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        ItemStack itemStack = player.getItemInHand(hand);

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ItemStack result = new ItemStack(Items.BOWL);

        ItemStack transformed = ItemUtils.createFilledResult(
                itemStack,
                player,
                result
        );

        return InteractionResult.SUCCESS.heldItemTransformedTo(transformed);
    }
}