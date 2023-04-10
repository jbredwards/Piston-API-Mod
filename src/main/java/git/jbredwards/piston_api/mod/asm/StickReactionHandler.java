package git.jbredwards.piston_api.mod.asm;

import git.jbredwards.piston_api.api.block.IStickyBehavior;
import git.jbredwards.piston_api.api.piston.EnumStickReaction;
import git.jbredwards.piston_api.api.piston.IPistonInfo;
import net.minecraft.block.Block;
import net.minecraft.dispenser.IBlockSource;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
public final class StickReactionHandler
{
    @Nonnull
    public static EnumStickReaction getStickReaction(@Nonnull IBlockSource source, @Nonnull IBlockSource other, @Nonnull IPistonInfo pistonInfo) {
        //handle possible override
        final ASMHandler.IBlockOverrides.OverridesHandler handler = ((ASMHandler.IBlockOverrides)source.getBlockState().getBlock()).getOverridesHandler();
        if(handler.stickyHandler != null) return handler.stickyHandler.getStickReaction(source, other, pistonInfo);

        //handle default behavior
        return source.getBlockState().getBlock() instanceof IStickyBehavior
                ? ((IStickyBehavior)source.getBlockState().getBlock()).getStickReaction(source, other, pistonInfo)
                : source.getBlockState().getBlock().isStickyBlock(source.getBlockState()) ? EnumStickReaction.STICK : EnumStickReaction.PASS;
    }

    public static boolean hasStickySide(@Nonnull IBlockSource source, @Nonnull IPistonInfo pistonInfo) {
        //handle possible override
        final ASMHandler.IBlockOverrides.OverridesHandler handler = ((ASMHandler.IBlockOverrides)source.getBlockState().getBlock()).getOverridesHandler();
        if(handler.stickyHandler != null) return handler.stickyHandler.hasStickySide(source, pistonInfo);

        //handle default behavior
        return source.getBlockState().getBlock() instanceof IStickyBehavior
                ? ((IStickyBehavior)source.getBlockState().getBlock()).hasStickySide(source, pistonInfo)
                : source.getBlockState().getBlock().isStickyBlock(source.getBlockState());
    }

    /**
     * Overrides a block's stickiness. Intended for modpack authors to use alongside GroovyScript or the like.
     */
    public static void overrideStickReaction(@Nonnull Block block, @Nonnull EnumStickReaction override) {
        ((ASMHandler.IBlockOverrides)block).getOverridesHandler().stickyHandler = override == EnumStickReaction.PASS || override == EnumStickReaction.NEVER
                ? new IStickyBehavior() {
                        @Nonnull
                        @Override
                        public EnumStickReaction getStickReaction(@Nonnull IBlockSource source, @Nonnull IBlockSource other, @Nonnull IPistonInfo pistonInfo) {
                            return override;
                        }

                        @Override
                        public boolean hasStickySide(@Nonnull IBlockSource source, @Nonnull IPistonInfo pistonInfo) { return false; }
                }
                : (source, other, structureHelper) -> override;
    }

    /**
     * Overrides a block's stickiness. Intended for modpack authors to use alongside GroovyScript or the like.
     */
    public static void overrideStickReaction(@Nonnull Block block, @Nonnull IStickyBehavior override) {
        ((ASMHandler.IBlockOverrides)block).getOverridesHandler().stickyHandler = override;
    }

    /**
     * Overrides multiple blocks stickiness. Intended for modpack authors to use alongside GroovyScript or the like.
     */
    public static void overrideStickReaction(@Nonnull Iterable<Block> blocks, @Nonnull EnumStickReaction override) {
        for(Block block : blocks) overrideStickReaction(block, override);
    }

    /**
     * Overrides multiple blocks stickiness. Intended for modpack authors to use alongside GroovyScript or the like.
     */
    public static void overrideStickReaction(@Nonnull Iterable<Block> blocks, @Nonnull IStickyBehavior override) {
        for(Block block : blocks) overrideStickReaction(block, override);
    }
}
