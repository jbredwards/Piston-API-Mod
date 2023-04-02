package git.jbredwards.piston_api.mod.piston;

import git.jbredwards.piston_api.api.piston.IPistonInfo;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

/**
 * A basic implementation of IPistonInfo
 * @author jbred
 *
 */
public class PistonInfo implements IPistonInfo
{
    @Nonnull public final BlockPos pistonPos;
    @Nonnull public final EnumFacing pistonFacing, moveDirection;

    public PistonInfo(@Nonnull BlockPos pistonPosIn, @Nonnull EnumFacing pistonFacingIn, @Nonnull EnumFacing moveDirectionIn) {
        pistonPos = pistonPosIn;
        pistonFacing = pistonFacingIn;
        moveDirection = moveDirectionIn;
    }

    /**
     * @return the BlockPos of the piston
     */
    @Nonnull
    @Override
    public BlockPos getPistonPos() { return pistonPos; }

    /**
     * @return the direction the piston is facing
     */
    @Nonnull
    @Override
    public EnumFacing getPistonFacing() { return pistonFacing; }

    /**
     * @return the current direction blocks are being pushed or pulled
     */
    @Nonnull
    @Override
    public EnumFacing getMoveDirection() { return moveDirection; }
}
