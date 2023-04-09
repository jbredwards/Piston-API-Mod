package git.jbredwards.piston_api.mod.capability;

import git.jbredwards.piston_api.mod.asm.ASMHandler;
import git.jbredwards.piston_api.mod.compat.quark.QuarkHandler;
import git.jbredwards.piston_api.mod.config.PistonAPIConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 *
 * @author jbred
 *
 */
public class AdditionalPistonData implements IAdditionalPistonData
{
    @Nullable
    protected NBTTagCompound tileNbt;

    @Nullable
    @SideOnly(Side.CLIENT)
    protected TileEntity tileForRender;

    @Override
    public void readAdditionalDataFromWorld(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        if(PistonAPIConfig.pushTileEntities) {
            final TileEntity tile = world.getTileEntity(pos);
            if(tile != null) {
                if(ASMHandler.isQuarkInstalled) QuarkHandler.tileEntityCallbackStart(tile);
                tileNbt = tile.serializeNBT();

                //hotfix for chest rendering
                if(tile instanceof TileEntityChest) {
                    final NBTTagCompound neighbors = new NBTTagCompound();
                    Optional.ofNullable(((TileEntityChest)tile).adjacentChestXPos).ifPresent(neighbor -> neighbors.setTag("XPos", neighbor.serializeNBT()));
                    Optional.ofNullable(((TileEntityChest)tile).adjacentChestXNeg).ifPresent(neighbor -> neighbors.setTag("XNeg", neighbor.serializeNBT()));
                    Optional.ofNullable(((TileEntityChest)tile).adjacentChestZPos).ifPresent(neighbor -> neighbors.setTag("ZPos", neighbor.serializeNBT()));
                    Optional.ofNullable(((TileEntityChest)tile).adjacentChestZNeg).ifPresent(neighbor -> neighbors.setTag("ZNeg", neighbor.serializeNBT()));
                    tileNbt.setTag("piston_api_chest_neighbors", neighbors);
                }

                world.removeTileEntity(pos);
            }
        }
    }

    @Override
    public void writeAdditionalDataToWorld(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int blockFlags) {
        if(tileNbt != null) {
            final TileEntity tile = TileEntity.create(world, tileNbt);
            if(tile != null) {
                if(ASMHandler.isQuarkInstalled) QuarkHandler.tileEntityCallbackFinish(tile);
                world.setTileEntity(pos, tile);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void preInitRender(@Nonnull TileEntityPiston tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if(tileNbt != null) {
            //create tile for rendering
            if(tileForRender == null) {
                tileForRender = TileEntity.create(tile.getWorld(), tileNbt);
                if(tileForRender != null) {
                    tileForRender.setWorld(tile.getWorld());
                    tileForRender.blockType = tile.getPistonState().getBlock();
                    tileForRender.blockMetadata = tileForRender.getBlockType().getMetaFromState(tile.getPistonState());

                    //ensure the correct chest neighbor data is rendered
                    if(tileForRender instanceof TileEntityChest) {
                        final NBTTagCompound neighbors = tileNbt.getCompoundTag("piston_api_chest_neighbors");
                        if(!neighbors.isEmpty()) {
                            ((TileEntityChest)tileForRender).adjacentChestChecked = true;
                            ((TileEntityChest)tileForRender).adjacentChestXPos = readChestNeighborForRender(tile.getWorld(), neighbors, "XPos");
                            ((TileEntityChest)tileForRender).adjacentChestXNeg = readChestNeighborForRender(tile.getWorld(), neighbors, "XNeg");
                            ((TileEntityChest)tileForRender).adjacentChestZPos = readChestNeighborForRender(tile.getWorld(), neighbors, "ZPos");
                            ((TileEntityChest)tileForRender).adjacentChestZNeg = readChestNeighborForRender(tile.getWorld(), neighbors, "ZNeg");
                        }
                    }
                }
            }

            //render tile using TESR
            if(tileForRender != null) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(x + tile.getOffsetX(partialTicks), y + tile.getOffsetY(partialTicks), z + tile.getOffsetZ(partialTicks));
                RenderHelper.enableStandardItemLighting();

                tileForRender.validate();
                TileEntityRendererDispatcher.instance.render(tileForRender, 0, 0, 0, partialTicks, destroyStage, alpha);

                RenderHelper.disableStandardItemLighting();
                GlStateManager.popMatrix();
            }
        }
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    protected TileEntityChest readChestNeighborForRender(@Nonnull World world, @Nonnull NBTTagCompound neighbors, @Nonnull String name) {
        return neighbors.hasKey(name, Constants.NBT.TAG_COMPOUND) ? (TileEntityChest)TileEntity.create(world, neighbors.getCompoundTag(name)) : null;
    }

    @Nonnull
    @Override
    public NBTTagCompound serializeNBT() {
        final NBTTagCompound nbt = new NBTTagCompound();
        if(tileNbt != null) nbt.setTag("TileNBT", tileNbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(@Nonnull NBTTagCompound nbt) {
        if(nbt.hasKey("TileNBT", Constants.NBT.TAG_COMPOUND)) tileNbt = nbt.getCompoundTag("TileNBT");
    }

    @SubscribeEvent
    static void attach(@Nonnull AttachCapabilitiesEvent<TileEntity> event) {
        if(event.getObject() instanceof TileEntityPiston) {
            event.addCapability(CAPABILITY_ID, new ICapabilitySerializable<NBTBase>() {
                final IAdditionalPistonData instance = CAPABILITY.getDefaultInstance();

                @Override
                public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                    return capability == CAPABILITY;
                }

                @Nullable
                @Override
                public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                    return hasCapability(capability, facing) ? CAPABILITY.cast(instance) : null;
                }

                @Nonnull
                @Override
                public NBTBase serializeNBT() { return CAPABILITY.writeNBT(instance, null); }

                @Override
                public void deserializeNBT(@Nonnull NBTBase nbt) { CAPABILITY.readNBT(instance, null, nbt); }
            });
        }
    }
}
