package git.jbredwards.piston_api.mod.capability.fluidlogged_api;

import git.jbredwards.fluidlogged_api.api.util.FluidState;
import git.jbredwards.fluidlogged_api.api.util.FluidloggedUtils;
import git.jbredwards.piston_api.mod.capability.AdditionalPistonData;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
public class AdditionalFluidPistonData extends AdditionalPistonData
{
    @Nonnull
    protected FluidState fluidState = FluidState.EMPTY;

    @SideOnly(Side.CLIENT)
    @Override
    public void preRender(@Nonnull TileEntityPiston tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if(!fluidState.isEmpty() && !FluidloggedUtils.isFluid(tile.getPistonState())) {
            final BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            buffer.begin(7, DefaultVertexFormats.BLOCK);
            buffer.setTranslation(
                    x - tile.getPos().getX() + tile.getOffsetX(partialTicks),
                    y - tile.getPos().getY() + tile.getOffsetY(partialTicks),
                    z - tile.getPos().getZ() + tile.getOffsetZ(partialTicks));


            final BlockRendererDispatcher renderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
            renderer.getBlockModelRenderer().renderModel(tile.getWorld(), renderer.getModelForState(fluidState.getState()), fluidState.getState(), tile.getPos(), buffer, false);
            buffer.setTranslation(0, 0, 0);
            Tessellator.getInstance().draw();
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound serializeNBT() {
        final NBTTagCompound nbt = super.serializeNBT();
        if(!fluidState.isEmpty()) nbt.setString("FluidState", fluidState.getBlock().delegate.name().toString());
        return nbt;
    }

    @Override
    public void deserializeNBT(@Nonnull NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        fluidState = FluidState.of(Block.getBlockFromName(nbt.getString("FluidState")));
    }
}
