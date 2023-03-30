package git.jbredwards.piston_api.api.block;

import git.jbredwards.piston_api.api.piston.IPistonStructureHelper;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
public interface IPushableBehavior
{
    @Nonnull
    EnumPushReaction getPushReaction(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull IPistonStructureHelper structureHelper);
}
