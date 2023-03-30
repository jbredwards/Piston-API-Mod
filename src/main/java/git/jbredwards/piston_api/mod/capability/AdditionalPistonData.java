package git.jbredwards.piston_api.mod.capability;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

    @SideOnly(Side.CLIENT)
    @Override
    public void postRender(@Nonnull TileEntityPiston tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if(tileNbt != null) {
            //create tile for rendering
            if(tileForRender == null) {
                tileForRender = TileEntity.create(tile.getWorld(), tileNbt);
                if(tileForRender != null) {
                    tileForRender.blockType = tile.getPistonState().getBlock();
                    tileForRender.blockMetadata = tileForRender.getBlockType().getMetaFromState(tile.getPistonState());
                }
            }

            //render tile using TESR
            if(tileForRender != null) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(x + tile.getOffsetX(partialTicks), y + tile.getOffsetY(partialTicks), z + tile.getOffsetZ(partialTicks));
                RenderHelper.enableStandardItemLighting();

                tileForRender.setWorld(tile.getWorld());
                tileForRender.validate();
                TileEntityRendererDispatcher.instance.render(tile, 0, 0, 0, partialTicks, destroyStage, alpha);

                RenderHelper.disableStandardItemLighting();
                GlStateManager.popMatrix();
            }
        }
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
}
