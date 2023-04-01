package git.jbredwards.piston_api.mod.asm;

import git.jbredwards.piston_api.api.block.IStickyBehavior;
import git.jbredwards.piston_api.api.piston.EnumStickResult;
import git.jbredwards.piston_api.api.piston.IPistonInfo;
import net.minecraft.block.Block;
import net.minecraft.dispenser.IBlockSource;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
public final class StickResultHandler
{
    @Nonnull
    public static EnumStickResult getStickResult(@Nonnull IBlockSource source, @Nonnull IBlockSource other, @Nonnull IPistonInfo pistonInfo) {
        //handle possible override
        final ASMHandler.IBlockOverrides.OverridesHandler handler = ((ASMHandler.IBlockOverrides)source.getBlockState().getBlock()).getOverridesHandler();
        if(handler.stickyHandler != null) return handler.stickyHandler.getStickResult(source, other, pistonInfo);

        //handle default behavior
        return source.getBlockState().getBlock() instanceof IStickyBehavior
                ? ((IStickyBehavior)source.getBlockState().getBlock()).getStickResult(source, other, pistonInfo)
                : source.getBlockState().getBlock().isStickyBlock(source.getBlockState()) ? EnumStickResult.STICK : EnumStickResult.PASS;
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
    public static void overrideStickiness(@Nonnull Block block, @Nonnull EnumStickResult override) {
        ((ASMHandler.IBlockOverrides)block).getOverridesHandler().stickyHandler = override == EnumStickResult.PASS || override == EnumStickResult.NEVER
                ? new IStickyBehavior() {
                        @Nonnull
                        @Override
                        public EnumStickResult getStickResult(@Nonnull IBlockSource source, @Nonnull IBlockSource other, @Nonnull IPistonInfo pistonInfo) {
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
    public static void overrideStickiness(@Nonnull Block block, @Nonnull IStickyBehavior override) {
        ((ASMHandler.IBlockOverrides)block).getOverridesHandler().stickyHandler = override;
    }
}
