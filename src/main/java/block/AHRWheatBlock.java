package block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

@Deprecated
public class AHRWheatBlock extends CropBlock {

    public AHRWheatBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    // Main random tick - cycle
    @Override
    protected void randomTick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random
    ) {

        if (!this.isMaxAge(state)) {

            if (random.nextInt(100) < 3) {
                System.out.println("пшеница dead");
                level.setBlock(pos, Blocks.DEAD_BUSH.defaultBlockState(), 2);
            } else {
                System.out.println("пшеница попытка роста: " + pos);
                int rnd = random.nextInt(2);
                if (rnd >= 1) { // 1 or 2 (66%)
                    level.setBlock(pos, this.getStateForAge(this.getAge(state) + 1), 2);
                    System.out.println("пшеница grow: " + pos + " [rnd: " + rnd + "]");
                } else { // (33%)
                    System.out.println("пшеница not grow: " + pos + " [rnd: " + rnd + "]");
                }
            }

        } else {
            if (random.nextInt(5) < 2) {
                System.out.println("пшеница dead");
                level.setBlock(pos, Blocks.DEAD_BUSH.defaultBlockState(), 2);
            }
        }
    }

    // grow func for bone meal
    @Override
    public void growCrops(final Level level, final BlockPos pos, final BlockState state) {
        int age = Math.min(this.getMaxAge(), this.getAge(state) + 1);
        System.out.println("ahr wheat age set to: " + age);
        level.setBlock(pos, this.getStateForAge(age), 2);
    }

    // random tick always active (for try to dead crop after full grow)
    @Override
    protected boolean isRandomlyTicking(final BlockState state) {
        return true;
    }
}
