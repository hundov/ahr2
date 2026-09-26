package entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import org.jspecify.annotations.Nullable;

public interface PhantomAHRAccess {

    @Nullable
    BlockPos ahr$getBombingTarget();

    void ahr$setBombingTarget(BlockPos target);

    void ahr$clearBombingTarget();

    DynamicGameEventListener<?> ahr$getDynamicGameEventListener();
}