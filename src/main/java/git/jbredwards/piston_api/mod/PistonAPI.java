package git.jbredwards.piston_api.mod;

import git.jbredwards.piston_api.mod.asm.PushReactionHandler;
import git.jbredwards.piston_api.mod.capability.AdditionalPistonData;
import git.jbredwards.piston_api.mod.capability.IAdditionalPistonData;
import git.jbredwards.piston_api.mod.capability.fluidlogged_api.AdditionalFluidPistonData;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.init.Blocks;
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
        PushReactionHandler.overridePushReaction(Blocks.ENDER_CHEST, EnumPushReaction.BLOCK);
        PushReactionHandler.overridePushReaction(Blocks.ENCHANTING_TABLE, EnumPushReaction.BLOCK);
    }
}
