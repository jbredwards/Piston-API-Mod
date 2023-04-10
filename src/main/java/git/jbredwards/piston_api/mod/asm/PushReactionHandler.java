package git.jbredwards.piston_api.mod.asm;

import git.jbredwards.piston_api.api.block.IPushableBehavior;
import git.jbredwards.piston_api.api.piston.IPistonInfo;
import git.jbredwards.piston_api.mod.config.PistonAPIConfig;
import git.jbredwards.piston_api.mod.piston.BlockSourceCache;
import git.jbredwards.piston_api.mod.piston.PistonInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.util.EnumFacing;
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
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean canPush(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing moveDirection, boolean destroyBlocks, @Nonnull EnumFacing pistonFacing, @Nonnull BlockPos pistonPos) {
        //check that the block isn't out of bounds, and that it won't be moved out of bounds
        if(!worldIn.getWorldBorder().contains(pos) || worldIn.isOutsideBuildHeight(pos)
        || moveDirection.getAxis().isVertical() && worldIn.isOutsideBuildHeight(pos.offset(moveDirection))) return false;

        //hardcoded checks
        else if(!PistonAPIConfig.pushTileEntities && state.getBlock().hasTileEntity(state)) return false;
        else if(state.getBlock() instanceof BlockPistonBase) return !state.getValue(BlockPistonBase.EXTENDED);
        else if(state.getBlockHardness(worldIn, pos) == -1) return false;

        //block-specific check
        switch(getPushReaction(new BlockSourceCache(worldIn, pos, state), new PistonInfo(pistonPos, pistonFacing, moveDirection))) {
            case BLOCK: return false;
            case DESTROY: return destroyBlocks;
            case PUSH_ONLY: return pistonFacing == moveDirection;
            default: return true;
        }
    }

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

    /**
     * Overrides multiple blocks' piston push reaction. Intended for modpack authors to use alongside GroovyScript or the like.
     */
    public static void overridePushReaction(@Nonnull Iterable<Block> blocks, @Nonnull EnumPushReaction override) {
        for(Block block : blocks) overridePushReaction(block, override);
    }

    /**
     * Overrides multiple blocks' piston push reaction. Intended for modpack authors to use alongside GroovyScript or the like.
     */
    public static void overridePushReaction(@Nonnull Iterable<Block> blocks, @Nonnull IPushableBehavior override) {
       for(Block block : blocks) overridePushReaction(block, override);
    }
}
