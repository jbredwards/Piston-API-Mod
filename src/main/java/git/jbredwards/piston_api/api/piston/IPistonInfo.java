package git.jbredwards.piston_api.api.piston;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

/**
 * Holds various info about an active piston, including its:
 * <p>
 * BlockPos, Orientation, and Current Push/Pull Direction
 * @author jbred
 *
 */
public interface IPistonInfo
{
    /**
     * @return the BlockPos of the piston
     */
    @Nonnull
    BlockPos getPistonPos();

    /**
     * @return the direction the piston is facing
     */
    @Nonnull
    EnumFacing getPistonFacing();

    /**
     * @return the current direction blocks are being pushed or pulled
     */
    @Nonnull
    EnumFacing getMoveDirection();

    /**
     * @return whether the piston is currently extending
     */
    default boolean isExtending() { return getPistonFacing() == getMoveDirection(); }
}
