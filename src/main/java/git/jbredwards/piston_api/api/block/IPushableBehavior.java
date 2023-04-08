package git.jbredwards.piston_api.api.block;

import git.jbredwards.piston_api.api.piston.IPistonInfo;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.dispenser.IBlockSource;

import javax.annotation.Nonnull;

/**
 * Blocks should implement this if they have advanced push reaction logic.
 * Implementing this gives access to the following:
 * <p>
 * the World, BlockPos, and Piston Info
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
    EnumPushReaction getPushReaction(@Nonnull IBlockSource source, @Nonnull IPistonInfo pistonInfo);
}
