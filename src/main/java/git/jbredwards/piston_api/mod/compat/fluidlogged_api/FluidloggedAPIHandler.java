package git.jbredwards.piston_api.mod.compat.fluidlogged_api;

import git.jbredwards.fluidlogged_api.api.util.FluidState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
public final class FluidloggedAPIHandler
{
    @Nonnull
    public static IBlockState getFluidOrAir(@Nonnull World world, @Nonnull BlockPos pos) {
        return FluidState.get(world, pos).getState();
    }
}
