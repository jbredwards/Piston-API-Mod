package git.jbredwards.piston_api.mod;

import git.jbredwards.piston_api.mod.asm.PushReactionHandler;
import git.jbredwards.piston_api.mod.capability.AdditionalPistonData;
import git.jbredwards.piston_api.mod.capability.IAdditionalPistonData;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nonnull;

@Mod(modid = "piston_api", name = "Piston API", version = "1.0.0")
public final class PistonAPI
{
    @Mod.EventHandler
    static void preInit(@Nonnull FMLPreInitializationEvent event) {
        CapabilityManager.INSTANCE.register(IAdditionalPistonData.class, IAdditionalPistonData.Storage.INSTANCE, AdditionalPistonData::new);
    }

    @Mod.EventHandler
    static void init(@Nonnull FMLInitializationEvent event) {
        //these blocks are obsidian, and thus shouldn't be pushable
        PushReactionHandler.overridePushReaction(Blocks.ENDER_CHEST, EnumPushReaction.BLOCK);
        PushReactionHandler.overridePushReaction(Blocks.ENCHANTING_TABLE, EnumPushReaction.BLOCK);
    }
}
