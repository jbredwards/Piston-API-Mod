package git.jbredwards.piston_api.api.block;

import git.jbredwards.piston_api.api.piston.IPistonStructureHelper;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Blocks should implement this if they should have advanced push reaction logic.
 * Implementing this gives access to the following:
 * <p>
 * the World, BlockPos, and Structure Helper
 * @author jbred
 *
 */
@FunctionalInterface
public interface IPushableBehavior
{
    /**
     * @return how this reacts when pushed by a piston
     */
    @Nonnull
    EnumPushReaction getPushReaction(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull IPistonStructureHelper structureHelper);
}
