package git.jbredwards.piston_api.mod.capability.fluidlogged_api;

import git.jbredwards.fluidlogged_api.api.capability.CapabilityProvider;
import git.jbredwards.fluidlogged_api.api.capability.IFluidStateCapability;
import git.jbredwards.fluidlogged_api.api.util.FluidState;
import git.jbredwards.fluidlogged_api.api.util.FluidloggedUtils;
import git.jbredwards.piston_api.mod.capability.AdditionalPistonData;
import git.jbredwards.piston_api.mod.config.PistonAPIConfig;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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

    @Override
    public void readAdditionalDataFromWorld(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        super.readAdditionalDataFromWorld(world, pos, state);
        if(PistonAPIConfig.pushFluidStates) {
            fluidState = FluidState.get(world, pos);
            //don't send changes to client to prevent desync, since this gets called by the client
            if(!fluidState.isEmpty() && FluidloggedUtils.setFluidState(world, pos, state, FluidState.EMPTY, false, false, Constants.BlockFlags.NOTIFY_NEIGHBORS) && world.isRemote) {
                //manually set the FluidState to empty
                final IFluidStateCapability cap = IFluidStateCapability.get(world.getChunk(pos));
                if(cap != null) cap.getContainer(pos).setFluidState(pos, FluidState.EMPTY);
            }
        }
    }

    @Override
    public void writeAdditionalDataToWorld(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int blockFlags) {
        super.writeAdditionalDataToWorld(world, pos, state, blockFlags);
        if(!fluidState.isEmpty()) {
            //check for air (should never happen, but you never know ¯\_(ツ)_/¯
            if(state.getBlock().isAir(state, world, pos)) world.setBlockState(pos, fluidState.getState(), blockFlags);
            else if(FluidloggedUtils.isStateFluidloggable(state, world, pos, fluidState.getFluid()))
                FluidloggedUtils.setFluidState(world, pos, state, fluidState, false, false, blockFlags);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void preBlockRender(@Nonnull TileEntityPiston tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if(!fluidState.isEmpty() && !FluidloggedUtils.isFluid(tile.getPistonState())) {
            final BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            buffer.begin(7, DefaultVertexFormats.BLOCK);
            buffer.setTranslation(
                    x - tile.getPos().getX() + tile.getOffsetX(partialTicks),
                    y - tile.getPos().getY() + tile.getOffsetY(partialTicks),
                    z - tile.getPos().getZ() + tile.getOffsetZ(partialTicks)
            );

            final BlockRendererDispatcher renderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
            renderer.getBlockModelRenderer().renderModel(tile.getWorld(), renderer.getModelForState(fluidState.getState()), fluidState.getState(), tile.getPos(), buffer, false);
            buffer.setTranslation(0, 0, 0);
            Tessellator.getInstance().draw();
        }

        /*GlStateManager.pushMatrix();
        GlStateManager.translate(x + tile.getOffsetX(partialTicks), y + tile.getOffsetY(partialTicks), z + tile.getOffsetZ(partialTicks));
        final float brightness = tile.getWorld().getCombinedLight(tile.getPos(), Math.max(
                fluidState.getState().getLightValue(tile.getWorld(), tile.getPos()),
                tile.getPistonState().getLightValue(tile.getWorld(), tile.getPos())
        ));

        Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(fluidState.getState(), brightness / 15f);
        GlStateManager.popMatrix();*/
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

    @SubscribeEvent
    static void attach(@Nonnull AttachCapabilitiesEvent<TileEntity> event) {
        if(event.getObject() instanceof TileEntityPiston) event.addCapability(CAPABILITY_ID,
                new CapabilityProvider<>(CAPABILITY, new AdditionalFluidPistonData()));
    }
}
