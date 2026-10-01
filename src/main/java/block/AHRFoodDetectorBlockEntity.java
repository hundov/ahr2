package block;

import food.AHRSpoilage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import registry.AHRBlockEntities;
import registry.AHRFoodTags;

public class AHRFoodDetectorBlockEntity extends BlockEntity implements Container {

    private final NonNullList<ItemStack> items =
            NonNullList.withSize(1, ItemStack.EMPTY);

    public AHRFoodDetectorBlockEntity(BlockPos pos, BlockState state) {
        super(AHRBlockEntities.FOOD_DETECTOR, pos, state);
    }

    @Override
    public int getContainerSize() {return 1;}

    @Override
    public boolean isEmpty() {return items.get(0).isEmpty();}

    @Override
    public ItemStack getItem(int slot) {return items.get(slot);}

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(items, slot, amount);

        if (!result.isEmpty()) {
            setChanged();
            updateRedstone();
        }

        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack result = ContainerHelper.takeItem(items, slot);

        if (!result.isEmpty()) {
            setChanged();
            updateRedstone();
        }

        return result;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        setChanged();
        updateRedstone();
    }

    @Override
    public void clearContent() {
        items.set(0, ItemStack.EMPTY);
        setChanged();
        updateRedstone();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot == 0
                && stack.is(AHRFoodTags.PERISHABLE_ITEM);
    }

    @Override
    public boolean stillValid(Player player) {
        return level != null
                && level.getBlockEntity(worldPosition) == this
                && player.distanceToSqr(
                worldPosition.getX() + 0.5,
                worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5
        ) <= 64.0;
    }

    public boolean isFoodFresh() {
        if (level == null) return false;

        ItemStack stack = getItem(0);

        if (stack.isEmpty()) return false;

        if (!stack.is(AHRFoodTags.PERISHABLE_ITEM)) {
            return false;
        }

        return !AHRSpoilage.isSpoiled(level, stack);
    }

    private void updateRedstone() {
        if (level != null) {
            level.updateNeighborsAt(
                    worldPosition,
                    getBlockState().getBlock()
            );
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        ContainerHelper.loadAllItems(input, items);

        if (level != null && !level.isClientSide()) updateRedstone();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, items);
        super.saveAdditional(output);
    }
}