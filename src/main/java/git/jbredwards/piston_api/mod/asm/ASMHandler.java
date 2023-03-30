package git.jbredwards.piston_api.mod.asm;

import git.jbredwards.piston_api.api.block.IPushableBehavior;
import git.jbredwards.piston_api.api.block.IStickyBehavior;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.tree.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ListIterator;
import java.util.Map;

/**
 *
 * @author jbred
 *
 */
@IFMLLoadingPlugin.SortingIndex(1002)
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.Name("Piston API Plugin")
public final class ASMHandler implements IFMLLoadingPlugin
{
    /**
     * This class exists because the launcher don't allow {@link IClassTransformer IClassTransformers}
     * to be the same class as {@link IFMLLoadingPlugin IFMLLoadingPlugins}
     */
    public static final class Transformer implements IClassTransformer, Opcodes
    {
        @Override
        public byte[] transform(@Nonnull String name, @Nonnull String transformedName, @Nonnull byte[] basicClass) {
            //handle IBlockOverrides
            if(transformedName.equals("net.minecraft.block.Block")) {
                final ClassNode classNode = new ClassNode();
                new ClassReader(basicClass).accept(classNode, 0);

                classNode.interfaces.add("git/jbredwards/piston_api/mod/asm/ASMHandler$IBlockOverrides");
                classNode.fields.add(new FieldNode(ACC_PUBLIC | ACC_FINAL, "piston_api_overrides", "Lgit/jbredwards/piston_api/mod/asm/ASMHandler$IBlockOverrides$OverridesHandler;", null, null));

                all:
                for(MethodNode method : classNode.methods) {
                    if(method.name.equals("<init>") && method.desc.equals("(Lnet/minecraft/block/material/Material;Lnet/minecraft/block/material/MapColor;)V")) {
                        for(ListIterator<AbstractInsnNode> it = method.instructions.iterator(); it.hasNext();) {
                            final AbstractInsnNode insn = it.next();
                            if(insn.getOpcode() == RETURN) {
                                method.instructions.insertBefore(insn, new VarInsnNode(ALOAD, 0));
                                method.instructions.insertBefore(insn, new TypeInsnNode(NEW, "git/jbredwards/piston_api/mod/asm/ASMHandler$IBlockOverrides$OverridesHandler"));
                                method.instructions.insertBefore(insn, new InsnNode(DUP));
                                method.instructions.insertBefore(insn, new MethodInsnNode(INVOKESPECIAL, "git/jbredwards/piston_api/mod/asm/ASMHandler$IBlockOverrides$OverridesHandler", "<init>", "()V", false));
                                method.instructions.insertBefore(insn, new FieldInsnNode(PUTFIELD, "net/minecraft/block/Block", "piston_api_overrides", "Lgit/jbredwards/piston_api/mod/asm/ASMHandler$IBlockOverrides$OverridesHandler;"));
                                break all;
                            }
                        }
                    }
                }

                final MethodNode getOverridesHandler = new MethodNode(ACC_PUBLIC, "getOverridesHandler", "()Lgit/jbredwards/piston_api/mod/asm/ASMHandler$IBlockOverrides$OverridesHandler;", null, null);
                final GeneratorAdapter getOverridesHandlerGen = new GeneratorAdapter(getOverridesHandler, ACC_PUBLIC, "getOverridesHandler", getOverridesHandler.desc);
                getOverridesHandlerGen.visitVarInsn(ALOAD, 0);
                getOverridesHandlerGen.visitFieldInsn(GETFIELD, "net/minecraft/block/Block", "piston_api_overrides", "Lgit/jbredwards/piston_api/mod/asm/ASMHandler$IBlockOverrides$OverridesHandler;");
                getOverridesHandlerGen.visitInsn(ARETURN);
                classNode.methods.add(getOverridesHandler);

                //writes the changes
                final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                classNode.accept(writer);
                return writer.toByteArray();
            }

            return basicClass;
        }
    }

    public static final class Hooks
    {

    }

    //internal
    public interface IBlockOverrides
    {
        @Nonnull
        OverridesHandler getOverridesHandler();
        final class OverridesHandler
        {
            @Nullable
            IPushableBehavior pushableHandler;

            @Nullable
            IStickyBehavior stickyHandler;
        }
    }

    @Nonnull
    @Override
    public String[] getASMTransformerClass() {
        return new String[] {"git.jbredwards.piston_api.mod.asm.ASMHandler$Transformer"};
    }

    @Nullable
    @Override
    public String getModContainerClass() { return null; }

    @Nullable
    @Override
    public String getSetupClass() { return null; }

    @Override
    public void injectData(@Nonnull Map<String, Object> data) {}

    @Nullable
    @Override
    public String getAccessTransformerClass() { return null; }
}
