package git.jbredwards.piston_api.api.piston;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

/**
 *
 * @author jbred
 *
 */
public interface IPistonStructureHelper
{
    /**
     * @return the current World instance
     */
    @Nonnull
    World getWorld();

    /**
     * @return the BlockPos of the piston causing the movement
     */
    @Nonnull
    BlockPos getPistonPos();

    /**
     * @return the current BlockPos of the block to move (the block in front of the piston)
     */
    @Nonnull
    BlockPos getBlockToMove();

    /**
     * @return the direction all blocks are being pushed or pulled
     */
    @Nonnull
    EnumFacing getMoveDirection();

    /**
     * @return the direction the piston is facing
     */
    @Nonnull
    EnumFacing getPistonFacing();

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
