package git.jbredwards.piston_api.mod.piston;

import git.jbredwards.piston_api.api.piston.IPistonStructureHelper;
import git.jbredwards.piston_api.api.piston.EnumStickResult;
import git.jbredwards.piston_api.mod.asm.PushReactionHandler;
import git.jbredwards.piston_api.mod.asm.StickResultHandler;
import git.jbredwards.piston_api.mod.config.PistonAPIConfig;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Heavily inspired by PistonStructureResolver from imaginary versions, with added functionality
 * @author jbred
 *
 */
public class PistonStructureHelper extends BlockPistonStructureHelper implements IPistonStructureHelper
{
    @Nonnull
    public final EnumFacing pistonFacing;
    public PistonStructureHelper(@Nonnull World worldIn, @Nonnull BlockPos posIn, @Nonnull EnumFacing pistonFacingIn, boolean extending) {
        super(worldIn, posIn, pistonFacingIn, extending);
        pistonFacing = pistonFacingIn;
    }

    @Override
    public boolean canMove() {
        toMove.clear();
        toDestroy.clear();

        final IBlockState stateToMove = world.getBlockState(blockToMove);
        if(!BlockPistonBase.canPush(stateToMove, world, blockToMove, moveDirection, false, pistonFacing)) {
            if(PushReactionHandler.getPushReaction(stateToMove, world, blockToMove, this) != EnumPushReaction.DESTROY) return false;
            toDestroy.add(blockToMove);
            return true;
        }

        if(!addBlockLine(blockToMove, moveDirection)) return false;
        for(BlockPos pos : toMove) if(StickResultHandler.hasStickySide(new BlockSourceCache(world, pos), this) && !addBranchingBlocks(pos))
            return false;

        return true;
    }

    @Override
    public boolean addBlockLine(@Nonnull BlockPos origin, @Nonnull EnumFacing side) {
        IBlockSource source = new BlockSourceCache(world, origin);

        if(origin.equals(pistonPos) || isSourceAir(source) || toMove.contains(origin)) return true;
        else if(!BlockPistonBase.canPush(source.getBlockState(), world, origin, moveDirection, false, side)) return true;

        int blocksBehind = 1;
        if(blocksBehind + toMove.size() > PistonAPIConfig.maxPushLimit) return false;

        //get the blocks behind the piston
        while(StickResultHandler.hasStickySide(source, this)) {
            final BlockPos pos = origin.offset(moveDirection.getOpposite(), blocksBehind);
            if(pos.equals(pistonPos)) break;

            final IBlockSource prevSource = source;
            source = new BlockSourceCache(world, pos);

            if(isSourceAir(source) || !canBlocksStick(source, prevSource) || !BlockPistonBase.canPush(source.getBlockState(), world, pos, moveDirection, false, moveDirection.getOpposite())) {
                break;
            }

            if(++blocksBehind + toMove.size() > PistonAPIConfig.maxPushLimit) return false;
        }

        //add the blocks behind the piston to the total count
        int blocksTotal = 0;
        for(int i = blocksBehind - 1; i >= 0; i--) {
            toMove.add(origin.offset(moveDirection.getOpposite(), i));
            blocksTotal++;
        }

        //add the blocks in front of the piston
        int blocksForward = 1;
        while(true) {
            final BlockPos pos = origin.offset(moveDirection, blocksForward);
            final int index = toMove.indexOf(pos);
            if(index > -1) {
                reorderListAtCollision(blocksTotal, index);
                for(int i = 0; i <= blocksForward + blocksTotal; i++) {
                    final BlockPos offset = toMove.get(i);
                    if(StickResultHandler.hasStickySide(new BlockSourceCache(world, offset), this) && !addBranchingBlocks(offset))
                        return false;
                }

                return true;
            }

            source = new BlockSourceCache(world, pos);
            if(isSourceAir(source)) return true;
            else if(pos.equals(pistonPos) || !BlockPistonBase.canPush(source.getBlockState(), world, pos, moveDirection, true, moveDirection)) return false;

            if(PushReactionHandler.getPushReaction(source.getBlockState(), world, pos, this) == EnumPushReaction.DESTROY) {
                toDestroy.add(pos);
                return true;
            }

            if(toMove.size() >= PistonAPIConfig.maxPushLimit) return false;
            toMove.add(pos);
            blocksForward++;
            blocksTotal++;
        }
    }

    @Override
    public boolean addBranchingBlocks(@Nonnull BlockPos fromPos) {
        final IBlockSource source = new BlockSourceCache(world, fromPos);
        for(EnumFacing side : EnumFacing.VALUES) {
            if(side.getAxis() != moveDirection.getAxis()) {
                final BlockPos offset = fromPos.offset(side);
                if(canBlocksStick(source, new BlockSourceCache(world, offset)) && !addBlockLine(offset, side))
                    return false;
            }
        }

        return true;
    }

    /**
     * utility method that exists because I'm lazy
     */
    public boolean isSourceAir(@Nonnull IBlockSource source) {
        return source.getBlockState().getBlock().isAir(source.getBlockState(), world, source.getBlockPos());
    }

    /**
     * @return whether the two sources can stick together
     */
    public boolean canBlocksStick(@Nonnull IBlockSource first, @Nonnull IBlockSource second) {
        final EnumStickResult firstResult = StickResultHandler.getStickResult(first, second, this);
        if(firstResult == EnumStickResult.ALWAYS) return true; //first source always sticks to the second

        final EnumStickResult secondResult = StickResultHandler.getStickResult(second, first, this);
        if(secondResult == EnumStickResult.ALWAYS) return true; //second source always sticks to the first

        //don't stick if either don't allow it
        if(firstResult == EnumStickResult.NEVER || secondResult == EnumStickResult.NEVER) return false;
        return firstResult == EnumStickResult.STICK || secondResult == EnumStickResult.STICK;
    }

    /**
     * @return the World instance
     */
    @Nonnull
    @Override
    public World getWorld() { return world; }

    /**
     * @return the BlockPos of the piston causing the movement
     */
    @Nonnull
    @Override
    public BlockPos getPistonPos() { return pistonPos; }

    /**
     * @return the current BlockPos of the block to move (the block in front of the piston)
     */
    @Nonnull
    @Override
    public BlockPos getBlockToMove() { return blockToMove; }

    /**
     * @return the direction all blocks are being pushed or pulled
     */
    @Nonnull
    @Override
    public EnumFacing getMoveDirection() { return moveDirection; }

    /**
     * @return the direction the piston is facing
     */
    @Nonnull
    @Override
    public EnumFacing getPistonFacing() { return pistonFacing; }
}
