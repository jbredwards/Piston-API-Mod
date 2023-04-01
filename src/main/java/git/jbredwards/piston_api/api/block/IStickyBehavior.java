package git.jbredwards.piston_api.api.block;

import git.jbredwards.piston_api.api.piston.EnumStickResult;
import git.jbredwards.piston_api.api.piston.IPistonInfo;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

/**
 * Blocks should implement this if they should have advanced stickiness logic.
 * Implementing this gives access to the following:
 * <p>
 * the World, BlockPos, Neighbor Info, EnumFacing, and Piston Info
 * @author jbred
 *
 */
@FunctionalInterface
public interface IStickyBehavior
{
    /**
     * @return whether this can stick to "other"
     */
    @Nonnull
    EnumStickResult getStickResult(@Nonnull IBlockSource source, @Nonnull IBlockSource other, @Nonnull IPistonInfo pistonInfo);

    /**
     * @return whether this has any sticky sides
     */
    default boolean hasStickySide(@Nonnull IBlockSource source, @Nonnull IPistonInfo pistonInfo) { return true; }

    /**
     * Utility method that returns the side connecting "source" and "other"
     * @throws IllegalArgumentException if the two IBlockSources are not connected
     */
    @Nonnull
    static EnumFacing getConnectingSide(@Nonnull IBlockSource source, @Nonnull IBlockSource other) {
        final BlockPos sourcePos = source.getBlockPos();
        final BlockPos otherPos = other.getBlockPos();

        for(EnumFacing side : EnumFacing.VALUES) if(sourcePos.offset(side).equals(otherPos)) return side;
        throw new IllegalArgumentException("Sources are not connected, please report this issue: {" + source + "} & {" + other + '}');
    }
}
