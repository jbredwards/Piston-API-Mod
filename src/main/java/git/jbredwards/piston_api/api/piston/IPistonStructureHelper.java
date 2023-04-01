package git.jbredwards.piston_api.api.piston;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.List;

/**
 *
 * @author jbred
 *
 */
public interface IPistonStructureHelper extends IPistonInfo
{
    /**
     * @return whether the piston can move blocks in the direction of {@link IPistonStructureHelper#getMoveDirection()}
     */
    boolean canMove();

    /**
     * @return the current collection of blocks this will move
     */
    @Nonnull
    List<BlockPos> getBlocksToMove();

    /**
     * @return the current collection of blocks this will destroy
     */
    @Nonnull
    List<BlockPos> getBlocksToDestroy();
}
