package git.jbredwards.piston_api.mod.asm;

import git.jbredwards.piston_api.api.block.IPushableBehavior;
import git.jbredwards.piston_api.api.piston.IPistonStructureHelper;
import net.minecraft.block.Block;
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
public final class PushReactionHandler
{
    @Nonnull
    public static EnumPushReaction getPushReaction(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull IPistonStructureHelper structureHelper) {
        //handle possible override
        final ASMHandler.IBlockOverrides.OverridesHandler handler = ((ASMHandler.IBlockOverrides)state.getBlock()).getOverridesHandler();
        if(handler.pushableHandler != null) return handler.pushableHandler.getPushReaction(state, world, pos, structureHelper);

        //handle default behavior
        return state.getBlock() instanceof IPushableBehavior
                ? ((IPushableBehavior)state.getBlock()).getPushReaction(state, world, pos, structureHelper)
                : state.getPushReaction();
    }

    /**
     * Overrides a block's piston push reaction. Intended for modpack authors to use alongside GroovyScript or the like.
     */
    public static void overridePushReaction(@Nonnull Block block, @Nonnull EnumPushReaction override) {
        ((ASMHandler.IBlockOverrides)block).getOverridesHandler().pushableHandler = (state, world, pos, structureHelper) -> override;
    }

    /**
     * Overrides a block's piston push reaction. Intended for modpack authors to use alongside GroovyScript or the like.
     */
    public static void overridePushReaction(@Nonnull Block block, @Nonnull IPushableBehavior override) {
        ((ASMHandler.IBlockOverrides)block).getOverridesHandler().pushableHandler = override;
    }
}
