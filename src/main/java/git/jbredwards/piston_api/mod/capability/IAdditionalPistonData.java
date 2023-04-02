package git.jbredwards.piston_api.mod.capability;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Saves and renders additional piston data, like moving TileEntities & FluidStates
 * @author jbred
 *
 */
@SuppressWarnings("ConstantConditions")
public interface IAdditionalPistonData extends INBTSerializable<NBTTagCompound>
{
    @CapabilityInject(IAdditionalPistonData.class)
    @Nonnull Capability<IAdditionalPistonData> CAPABILITY = null;
    @Nonnull ResourceLocation CAPABILITY_ID = new ResourceLocation("piston_api", "additional_data");

    void readAdditionalDataFromWorld(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state);
    void writeAdditionalDataToWorld(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state);

    /**
     * Rendered before any opengl stuff is set up (right above line 37 in
     * {@link net.minecraft.client.renderer.tileentity.TileEntityPistonRenderer TileEntityPistonRenderer})
     */
    @SideOnly(Side.CLIENT)
    default void preInitRender(@Nonnull TileEntityPiston tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {}

    /**
     * Rendered before any opengl stuff is set up (right above line 37 in
     * {@link net.minecraft.client.renderer.tileentity.TileEntityPistonRenderer TileEntityPistonRenderer})
     */
    @SideOnly(Side.CLIENT)
    default void initRender(@Nonnull TileEntityPiston tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {}

    @Nullable
    static IAdditionalPistonData get(@Nullable ICapabilityProvider p) {
        return p != null && p.hasCapability(CAPABILITY, null) ? p.getCapability(CAPABILITY, null) : null;
    }

    enum Storage implements Capability.IStorage<IAdditionalPistonData>
    {
        INSTANCE;

        @Nonnull
        @Override
        public NBTBase writeNBT(@Nonnull Capability<IAdditionalPistonData> capability, @Nonnull IAdditionalPistonData instance, @Nullable EnumFacing side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(@Nonnull Capability<IAdditionalPistonData> capability, @Nonnull IAdditionalPistonData instance, @Nullable EnumFacing side, @Nullable NBTBase nbt) {
            if(nbt instanceof NBTTagCompound) instance.deserializeNBT((NBTTagCompound)nbt);
        }
    }
}
