package git.jbredwards.piston_api.mod.piston;

import net.minecraft.block.BlockSourceImpl;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * An implementation of IBlockSource that saves the IBlockState & TileEntity as they're needed.
 * @author jbred
 *
 */
@Immutable
public class BlockSourceCache extends BlockSourceImpl
{
    @Nullable
    public IBlockState state;

    @Nullable
    public TileEntity tile;
    protected boolean savedTile = false;

    public BlockSourceCache(@Nonnull World worldIn, @Nonnull BlockPos posIn) { super(worldIn, posIn); }
    public BlockSourceCache(@Nonnull World worldIn, @Nonnull BlockPos posIn, @Nonnull IBlockState stateIn) {
        super(worldIn, posIn);
        state = stateIn;
    }

    public BlockSourceCache(@Nonnull World worldIn, @Nonnull BlockPos posIn, @Nonnull IBlockState stateIn, @Nonnull TileEntity tileIn) {
        this(worldIn, posIn, stateIn);
        tile = tileIn;
        savedTile = true;
    }

    @Nonnull
    @Override
    public IBlockState getBlockState() { return state != null ? state : (state = super.getBlockState()); }

    @Nullable
    @Override
    public <T extends TileEntity> T getBlockTileEntity() {
        if(savedTile) return (T)tile;
        savedTile = true;

        return (T)(tile = super.getBlockTileEntity());
    }

    @Nonnull
    @Override
    public String toString() {
        return "BlockSourceCache{" +
                "pos=" + getBlockPos() +
                ", state=" + getBlockState() +
                ", tile=" + getBlockTileEntity() +
                '}';
    }
}
