package git.jbredwards.piston_api.mod.compat.quark;

import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import vazkii.quark.base.asm.ClassTransformer;

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
}
