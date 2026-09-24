package food.cake;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import registry.AHRBlockEntities;

public class CakeBlockEntity extends BlockEntity {

    private int madeOn;

    public CakeBlockEntity(BlockPos pos, BlockState state) {
        super(AHRBlockEntities.CAKE, pos, state);
    }

    public int getMadeOn() {return madeOn;}

    public void setMadeOn(int madeOn) {
        this.madeOn = madeOn;
        setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("madeOn", madeOn);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        madeOn = input.getIntOr("madeOn", 0);
    }
}
