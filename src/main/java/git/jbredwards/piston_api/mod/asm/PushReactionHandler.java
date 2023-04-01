package git.jbredwards.piston_api.mod.asm;

import git.jbredwards.piston_api.api.block.IPushableBehavior;
import git.jbredwards.piston_api.api.piston.IPistonInfo;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.dispenser.IBlockSource;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
public final class PushReactionHandler
{
    @Nonnull
    public static EnumPushReaction getPushReaction(@Nonnull IBlockSource source, @Nonnull IPistonInfo pistonInfo) {
        //handle possible override
        final ASMHandler.IBlockOverrides.OverridesHandler handler = ((ASMHandler.IBlockOverrides)source.getBlockState().getBlock()).getOverridesHandler();
        if(handler.pushableHandler != null) return handler.pushableHandler.getPushReaction(source, pistonInfo);

        //handle default behavior
        return source.getBlockState().getBlock() instanceof IPushableBehavior
                ? ((IPushableBehavior)source.getBlockState().getBlock()).getPushReaction(source, pistonInfo)
                : source.getBlockState().getPushReaction();
    }

    /**
     * Overrides a block's piston push reaction. Intended for modpack authors to use alongside GroovyScript or the like.
     */
    public static void overridePushReaction(@Nonnull Block block, @Nonnull EnumPushReaction override) {
        ((ASMHandler.IBlockOverrides)block).getOverridesHandler().pushableHandler = (source, pistonInfo) -> override;
    }

    /**
     * Overrides a block's piston push reaction. Intended for modpack authors to use alongside GroovyScript or the like.
     */
    public static void overridePushReaction(@Nonnull Block block, @Nonnull IPushableBehavior override) {
        ((ASMHandler.IBlockOverrides)block).getOverridesHandler().pushableHandler = override;
    }
}
