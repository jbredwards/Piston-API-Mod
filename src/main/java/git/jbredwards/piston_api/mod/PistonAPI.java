package git.jbredwards.piston_api.mod;

import git.jbredwards.piston_api.mod.asm.PushReactionHandler;
import git.jbredwards.piston_api.mod.capability.AdditionalPistonData;
import git.jbredwards.piston_api.mod.capability.IAdditionalPistonData;
import git.jbredwards.piston_api.mod.capability.fluidlogged_api.AdditionalFluidPistonData;
import git.jbredwards.piston_api.mod.compat.fluidlogged_api.FluidloggedAPIHandler;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
@Mod(modid = "piston_api", name = "Piston API", version = "1.0.0", dependencies = "after:fluidlogged_api@[2.1.2,);after:quark@[r1.6-180,)")
public final class PistonAPI
{
    public static final boolean hasFluidloggedAPI = Loader.isModLoaded("fluidlogged_api");

    @Mod.EventHandler
    static void preInit(@Nonnull FMLPreInitializationEvent event) {
        CapabilityManager.INSTANCE.register(IAdditionalPistonData.class, IAdditionalPistonData.Storage.INSTANCE, AdditionalPistonData::new);
        if(hasFluidloggedAPI) MinecraftForge.EVENT_BUS.register(AdditionalFluidPistonData.class);
        else MinecraftForge.EVENT_BUS.register(AdditionalPistonData.class);
    }

    @Mod.EventHandler
    static void init(@Nonnull FMLInitializationEvent event) {
        //these blocks are obsidian, and thus shouldn't be pushable
        PushReactionHandler.overridePushReaction(Blocks.OBSIDIAN, EnumPushReaction.BLOCK);
        PushReactionHandler.overridePushReaction(Blocks.ENDER_CHEST, EnumPushReaction.BLOCK);
        PushReactionHandler.overridePushReaction(Blocks.ENCHANTING_TABLE, EnumPushReaction.BLOCK);
    }

    @Nonnull
    public static IBlockState getFluidOrAir(@Nonnull World world, @Nonnull BlockPos pos) {
        return hasFluidloggedAPI ? FluidloggedAPIHandler.getFluidOrAir(world, pos) : Blocks.AIR.getDefaultState();
    }
}
