package block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import registry.AHRBlockEntities;
import util.Logger;

public class AHRCropBlockEntity extends BlockEntity {

    private int subStage = 0;


    private boolean boneMealUsed = false;
    private boolean boneMealEffect = false;

    protected int deathChance = 0;

    // Добавляемый шанс смерти за каждый тик при полном росте
    protected int fullyGrownDeathChance = 5;

    // Максимальное количество подстадий
    protected static final int maxSubStage = 3;

    private final Logger log = new Logger();

    public AHRCropBlockEntity(BlockPos pos, BlockState state) {
        super(AHRBlockEntities.CROP, pos, state);
    }

    public void tickGrowth(ServerLevel level, RandomSource random) {

        log.send("called");

        if (fullyGrownTick(level, random)) return;
        conditionTest(level, random);
        if (tryDeath(level, random)) return;
        if (random.nextInt(3) < (boneMealEffect ? 2 : 1)) advanceSubStage(level);

        log.send("end");
    }

    protected boolean fullyGrownTick(ServerLevel level, RandomSource random) {

        log.send("called");

        BlockState state = getBlockState();
        if (state.getValue(AHRCropBlock.AGE) >= AHRCropBlock.MAX_AGE) {
            deathChance += fullyGrownDeathChance;
            conditionTest(level, random);
            tryDeath(level, random);
            log.send("end. fullyGrownTick");
            return true;
        }

        log.send("end");
        return false;
    }

    protected void conditionTest(ServerLevel level, RandomSource random) {
        log.send("called");
        if (level.getRawBrightness(getBlockPos(), 0) < 9) deathChance++;
        log.send("end");
    }

    protected boolean tryDeath(ServerLevel level, RandomSource random) {
        log.send("called");
        if (random.nextInt(100) < deathChance) {
            death(level);
            log.send("end. died");
            return true;
        }
        log.send("end");
        return false;
    }

    protected void death(ServerLevel level) {
        log.send("called");
        BlockPos pos = getBlockPos();
        level.setBlock(pos, Blocks.DEAD_BUSH.defaultBlockState(), 2);
        setChanged();
        log.send("end");
    }

    protected void applyBoneMeal(ServerLevel level) {
        log.send("called");

        if (boneMealUsed) {
            log.send("end. boneMealUsed");
            return;
        }

        deathChance += 6;
        boneMealUsed = true;
        boneMealEffect = true;
        advanceSubStage(level);
        log.send("end");
    }

    protected void advanceSubStage(ServerLevel level) {
        log.send("called");

        subStage++;

        if (subStage >= maxSubStage) {
            subStage = 0;
            boneMealUsed = false;

            BlockState state = getBlockState();
            int age = state.getValue(AHRCropBlock.AGE);

            level.setBlock(
                    getBlockPos(),
                    state.setValue(AHRCropBlock.AGE, age + 1),
                    2
            );
        }

        setChanged();
        log.send("end");
    }

    // -- getters
    public boolean isBoneMealUsed() {return boneMealUsed;}
    public int getSubStage() {return subStage;}
    public int getDeathChance() {return deathChance;}

}
