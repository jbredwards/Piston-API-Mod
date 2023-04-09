package git.jbredwards.piston_api.mod.compat.quark;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import vazkii.quark.api.IPistonCallback;
import vazkii.quark.base.asm.ClassTransformer;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 *
 * @author jbred
 *
 */
public final class QuarkHandler
{
    public static void removeConflictingQuarkTransformers() {
        final Map<String, ?> transformers = ObfuscationReflectionHelper.getPrivateValue(ClassTransformer.class, null, "transformers");
        transformers.remove("net.minecraft.block.BlockPistonBase");
        transformers.remove("net.minecraft.client.renderer.tileentity.TileEntityPistonRenderer");
        transformers.remove("net.minecraft.tileentity.TileEntityPiston");
    }

    public static void tileEntityCallbackStart(@Nonnull TileEntity tile) {
        if(IPistonCallback.hasCallback(tile)) IPistonCallback.getCallback(tile).onPistonMovementStarted();
    }

    public static void tileEntityCallbackFinish(@Nonnull TileEntity tile) {
        if(IPistonCallback.hasCallback(tile)) IPistonCallback.getCallback(tile).onPistonMovementFinished();
    }
}
