package block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class AHRCropBlock extends CropBlock implements EntityBlock {

    private final int maxAge;
    private final ShapeType shapeType;

    private static final VoxelShape[] WHEAT_SHAPES =
            Block.boxes(7, age -> Block.column(16, 0, 2 + age * 2));

    private static final VoxelShape[] CARROT_SHAPES =
            Block.boxes(7, age -> Block.column(16, 0, 2 + age));

    private static final VoxelShape[] POTATO_SHAPES =
            Block.boxes(7, age -> Block.column(16, 0, 2 + age));

    private static final VoxelShape[] BEETROOT_SHAPES =
            Block.boxes(3, age -> Block.column(16, 0, 2 + age * 2));

    public AHRCropBlock(
            BlockBehaviour.Properties properties,
            int maxAge,
            ShapeType shapeType
    ) {
        super(properties);
        this.maxAge = maxAge;
        this.shapeType = shapeType;
    }

    @Override
    public int getMaxAge() {
        return maxAge;
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return switch (shapeType) {
            case WHEAT -> WHEAT_SHAPES[getAge(state)];
            case CARROT -> CARROT_SHAPES[getAge(state)];
            case POTATO -> POTATO_SHAPES[getAge(state)];
            case BEETROOT -> BEETROOT_SHAPES[getAge(state)];
        };
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new AHRCropBlockEntity(worldPosition, blockState);
    }

    // Main random tick - cycle
    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof AHRCropBlockEntity crop) {
            crop.tickGrowth(level, random);
        }
    }

    // включаем постоянный тик для смерти после роста
    @Override
    protected boolean isRandomlyTicking(final BlockState state) {
        return true;
    }

    // костная мука

    @Override
    public void performBonemeal(final ServerLevel level, final RandomSource random, final BlockPos pos, final BlockState state) {
        if (level.getBlockEntity(pos) instanceof AHRCropBlockEntity crop) crop.applyBoneMeal(level, random);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        if (this.getAge(state) >= this.getMaxAge()) return false;
        if (level.getBlockEntity(pos) instanceof AHRCropBlockEntity crop) return !crop.isBoneMealUsed();
        return false;
    }

    public enum ShapeType {
        WHEAT,
        CARROT,
        POTATO,
        BEETROOT
    }

}
