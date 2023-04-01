package git.jbredwards.piston_api.mod.asm;

import git.jbredwards.piston_api.api.block.IPushableBehavior;
import git.jbredwards.piston_api.api.block.IStickyBehavior;
import git.jbredwards.piston_api.api.piston.EnumStickResult;
import git.jbredwards.piston_api.api.piston.IPistonInfo;
import git.jbredwards.piston_api.api.piston.IPistonStructureHelper;
import git.jbredwards.piston_api.mod.PistonAPI;
import git.jbredwards.piston_api.mod.capability.IAdditionalPistonData;
import git.jbredwards.piston_api.mod.compat.fluidlogged_api.FluidloggedAPIHandler;
import git.jbredwards.piston_api.mod.compat.quark.QuarkHandler;
import git.jbredwards.piston_api.mod.piston.PistonStructureHelper;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.init.Blocks;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.tree.*;
import vazkii.quark.api.INonSticky;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 *
 * @author jbred
 *
 */
@IFMLLoadingPlugin.SortingIndex(1001)
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
        @Nonnull
        @Override
        public byte[] transform(@Nonnull String name, @Nonnull String transformedName, @Nonnull byte[] basicClass) {
            //mod support
            //TODO final boolean isICollateralMover = "vazkii.quark.api.ICollateralMover".equals(transformedName);
            final boolean isINonSticky = "vazkii.quark.api.INonSticky".equals(transformedName); //INonSticky extends IStickyBehavior

            //vanilla
            final boolean isBlock = "net.minecraft.block.Block".equals(transformedName); //handle IBlockOverrides
            final boolean isBlockPistonBase = "net.minecraft.block.BlockPistonBase".equals(transformedName); //movable TEs, optimizations, and new PistonStructureHelper implementation
            final boolean isBlockPistonExtension = "net.minecraft.block.BlockPistonExtension".equals(transformedName); //add piston head collisionRayTrace
            final boolean isTileEntityPistonRenderer = "net.minecraft.client.renderer.tileentity.TileEntityPistonRenderer".equals(transformedName);
            final boolean isTileEntityPiston = "net.minecraft.tileentity.TileEntityPiston".equals(transformedName);

            if(isINonSticky || isBlock || isBlockPistonBase || isBlockPistonExtension || isTileEntityPistonRenderer || isTileEntityPiston) {
                final ClassNode classNode = new ClassNode();
                new ClassReader(basicClass).accept(classNode, 0);

                //quark's INonSticky interface extends IStickyBehavior, so it has compat with this mod
                if(isINonSticky) {
                    classNode.interfaces.add("git/jbredwards/piston_api/api/block/IStickyBehavior");

                    final MethodNode getStickResult = new MethodNode(ACC_PUBLIC, "getStickResult", "(Lnet/minecraft/dispenser/IBlockSource;Lnet/minecraft/dispenser/IBlockSource;Lgit/jbredwards/piston_api/api/piston/IPistonInfo;)Lgit/jbredwards/piston_api/api/piston/EnumStickResult;", null, null);
                    final GeneratorAdapter getStickResultGen = new GeneratorAdapter(getStickResult, ACC_PUBLIC, "getStickResult", getStickResult.desc);
                    getStickResultGen.visitVarInsn(ALOAD, 0);
                    getStickResultGen.visitVarInsn(ALOAD, 1);
                    getStickResultGen.visitVarInsn(ALOAD, 2);
                    getStickResultGen.visitVarInsn(ALOAD, 3);
                    getStickResultGen.visitMethodInsn(INVOKESTATIC, "git/jbredwards/piston_api/mod/asm/ASMHandler$QuarkHooks", "getStickResult", "(Lvazkii/quark/api/INonSticky;Lnet/minecraft/dispenser/IBlockSource;Lnet/minecraft/dispenser/IBlockSource;Lgit/jbredwards/piston_api/api/piston/IPistonInfo;)Lgit/jbredwards/piston_api/api/piston/EnumStickResult;", false);
                    getStickResultGen.visitInsn(ARETURN);

                    final MethodNode hasStickySide = new MethodNode(ACC_PUBLIC, "hasStickySide", "(Lnet/minecraft/dispenser/IBlockSource;Lgit/jbredwards/piston_api/api/piston/IPistonInfo;)Z", null, null);
                    final GeneratorAdapter hasStickySideGen = new GeneratorAdapter(hasStickySide, ACC_PUBLIC, "hasStickySide", hasStickySide.desc);
                    hasStickySideGen.visitVarInsn(ALOAD, 1);
                    hasStickySideGen.visitMethodInsn(INVOKESTATIC, "git/jbredwards/piston_api/mod/asm/ASMHandler$QuarkHooks", "hasStickySide", "(Lnet/minecraft/dispenser/IBlockSource;)Z", false);
                    hasStickySideGen.visitInsn(IRETURN);

                    classNode.methods.add(getStickResult);
                    classNode.methods.add(hasStickySide);
                }

                //handle IBlockOverrides
                else if(isBlock) {
                    classNode.interfaces.add("git/jbredwards/piston_api/mod/asm/ASMHandler$IBlockOverrides");
                    classNode.fields.add(new FieldNode(ACC_PRIVATE | ACC_FINAL, "piston_api_overrides", "Lgit/jbredwards/piston_api/mod/asm/ASMHandler$IBlockOverrides$OverridesHandler;", null, null));

                    final MethodNode getOverridesHandler = new MethodNode(ACC_PUBLIC, "getOverridesHandler", "()Lgit/jbredwards/piston_api/mod/asm/ASMHandler$IBlockOverrides$OverridesHandler;", null, null);
                    final GeneratorAdapter getOverridesHandlerGen = new GeneratorAdapter(getOverridesHandler, ACC_PUBLIC, "getOverridesHandler", getOverridesHandler.desc);
                    getOverridesHandlerGen.visitVarInsn(ALOAD, 0);
                    getOverridesHandlerGen.visitFieldInsn(GETFIELD, "net/minecraft/block/Block", "piston_api_overrides", "Lgit/jbredwards/piston_api/mod/asm/ASMHandler$IBlockOverrides$OverridesHandler;");
                    getOverridesHandlerGen.visitInsn(ARETURN);

                    classNode.methods.add(getOverridesHandler);
                }

                //fix MC-124459
                else if(isBlockPistonBase) {
                    final MethodNode collisionRayTrace = new MethodNode(ACC_PUBLIC, FMLLaunchHandler.isDeobfuscatedEnvironment() ? "collisionRayTrace" : "func_180636_a", "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/RayTraceResult;", null, null);
                    final GeneratorAdapter collisionRayTraceGen = new GeneratorAdapter(collisionRayTrace, ACC_PUBLIC, collisionRayTrace.name, collisionRayTrace.desc);
                    collisionRayTraceGen.visitVarInsn(ALOAD, 0);
                    collisionRayTraceGen.visitVarInsn(ALOAD, 1);
                    collisionRayTraceGen.visitVarInsn(ALOAD, 2);
                    collisionRayTraceGen.visitVarInsn(ALOAD, 3);
                    collisionRayTraceGen.visitVarInsn(ALOAD, 4);
                    collisionRayTraceGen.visitVarInsn(ALOAD, 5);
                    collisionRayTraceGen.visitMethodInsn(INVOKESTATIC, "git/jbredwards/piston_api/mod/asm/ASMHandler$Hooks", "collisionRayTraceBase", "(Lnet/minecraft/block/BlockPistonBase;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/RayTraceResult;", false);
                    collisionRayTraceGen.visitInsn(ARETURN);

                    classNode.methods.add(collisionRayTrace);
                }

                //handle rayTrace
                else if(isBlockPistonExtension) {
                    final MethodNode collisionRayTrace = new MethodNode(ACC_PUBLIC, FMLLaunchHandler.isDeobfuscatedEnvironment() ? "collisionRayTrace" : "func_180636_a", "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/RayTraceResult;", null, null);
                    final GeneratorAdapter collisionRayTraceGen = new GeneratorAdapter(collisionRayTrace, ACC_PUBLIC, collisionRayTrace.name, collisionRayTrace.desc);
                    collisionRayTraceGen.visitVarInsn(ALOAD, 0);
                    collisionRayTraceGen.visitVarInsn(ALOAD, 1);
                    collisionRayTraceGen.visitVarInsn(ALOAD, 2);
                    collisionRayTraceGen.visitVarInsn(ALOAD, 3);
                    collisionRayTraceGen.visitVarInsn(ALOAD, 4);
                    collisionRayTraceGen.visitVarInsn(ALOAD, 5);
                    collisionRayTraceGen.visitMethodInsn(INVOKESTATIC, "git/jbredwards/piston_api/mod/asm/ASMHandler$Hooks", "collisionRayTraceHead", "(Lnet/minecraft/block/BlockPistonExtension;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/RayTraceResult;", false);
                    collisionRayTraceGen.visitInsn(ARETURN);

                    classNode.methods.add(collisionRayTrace);
                }

                //iterates through all the methods in the class to find the ones that have to be transformed
                if(isBlock || isBlockPistonBase || isTileEntityPistonRenderer || isTileEntityPiston) {
                    all:
                    for(MethodNode method : classNode.methods) {
                        final boolean isBlockConstructor = isBlock && method.name.equals("<init>") && method.desc.equals("(Lnet/minecraft/block/material/Material;Lnet/minecraft/block/material/MapColor;)V");
                        final boolean isBlockPistonBaseAddCollisionBoxToList = isBlock && method.name.equals(FMLLaunchHandler.isDeobfuscatedEnvironment() ? "addCollisionBoxToList" : "func_185477_a");

                        if(isBlockConstructor) {
                            for(ListIterator<AbstractInsnNode> it = method.instructions.iterator(); it.hasNext();) {
                                final AbstractInsnNode insn = it.next();
                                if(isBlockConstructor) {
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
                    }
                }

                //writes the changes
                final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                classNode.accept(writer);
                return writer.toByteArray();
            }

            return basicClass;
        }
    }

    @SuppressWarnings("unused")
    public static final class Hooks
    {
        @Nullable
        public static RayTraceResult collisionRayTraceBase(@Nonnull BlockPistonBase block, @Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end) {
            final RayTraceResult baseTrace = block.rayTrace(pos, start, end, state.getBoundingBox(world, pos));
            if(!state.getValue(BlockPistonBase.EXTENDED)) return baseTrace;
            final RayTraceResult armTrace = block.rayTrace(pos, start, end, getArmBaseBB(state));

            if(baseTrace == null) return armTrace;
            else if(armTrace == null) return baseTrace;
            else return baseTrace.hitVec.squareDistanceTo(end) > armTrace.hitVec.squareDistanceTo(end) ? baseTrace : armTrace;
        }

        //helper
        @Nonnull
        static AxisAlignedBB getArmBaseBB(@Nonnull IBlockState state) {
            switch(state.getValue(BlockPistonBase.FACING)) {
                case UP: return new AxisAlignedBB(0.375, 0.75, 0.375, 0.625, 1, 0.625);
                case DOWN: return new AxisAlignedBB(0.375, 0, 0.375, 0.625, 0.25, 0.625);
                case NORTH: return new AxisAlignedBB(0.375, 0.375, 0, 0.625, 0.625, 0.25);
                case SOUTH: return new AxisAlignedBB(0.375, 0.375, 0.75, 0.625, 0.625, 1);
                case EAST: return new AxisAlignedBB(0.75, 0.375, 0.375, 1, 0.625, 0.625);
                default: return new AxisAlignedBB(0, 0.375, 0.375, 0.25, 0.625, 0.625);
            }
        }

        @Nullable
        public static RayTraceResult collisionRayTraceHead(@Nonnull BlockPistonExtension block, @Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end) {
            final RayTraceResult headTrace = block.rayTrace(pos, start, end, state.getBoundingBox(world, pos));
            final RayTraceResult armTrace = block.rayTrace(pos, start, end, block.getArmShape(state));

            if(headTrace == null) return armTrace;
            else if(armTrace == null) return headTrace;
            else return headTrace.hitVec.squareDistanceTo(end) > armTrace.hitVec.squareDistanceTo(end) ? headTrace : armTrace;
        }

        public static boolean doMove(@Nonnull World worldIn, @Nonnull BlockPos origin, @Nonnull EnumFacing direction, boolean extending, boolean isSticky) {
            if(!extending) worldIn.setBlockToAir(origin.offset(direction));

            final IPistonStructureHelper structureHelper = new PistonStructureHelper(worldIn, origin, direction, extending);
            if(!structureHelper.canMove()) return false;

            final List<BlockPos> blocksToMove = structureHelper.getBlocksToMove();
            final List<BlockPos> blocksToDestroy = structureHelper.getBlocksToDestroy();

            int blocksToHandle = blocksToMove.size() + blocksToDestroy.size();
            final Block[] blocksHandled = new Block[blocksToHandle];

            //handle blocksToDestroy
            for(int i = blocksToDestroy.size() - 1; i >= 0; i--) {
                final BlockPos pos = blocksToDestroy.get(i);
                final IBlockState state = worldIn.getBlockState(pos);
                // Forge: With our change to how snowballs are dropped this needs to disallow to mimic vanilla behavior.
                final float chance = state.getBlock() instanceof BlockSnow ? -1 : 1;
                state.getBlock().dropBlockAsItemWithChance(worldIn, pos, state, chance, 0);
                worldIn.setBlockState(pos, PistonAPI.hasFluidloggedAPI ? FluidloggedAPIHandler.getFluidOrAir(worldIn, pos) : Blocks.AIR.getDefaultState(), 4);

                blocksHandled[--blocksToHandle] = state.getBlock();
            }

            //handle blocksToMove
            for(int i = blocksToMove.size() - 1; i >= 0; i--) {
                final BlockPos pos = blocksToDestroy.get(i);
                final IBlockState state = worldIn.getBlockState(pos).getActualState(worldIn, pos);

                final TileEntity pistonTile = BlockPistonMoving.createTilePiston(state, direction, extending, false);
                final IAdditionalPistonData cap = IAdditionalPistonData.get(pistonTile);
                if(cap != null) cap.readAdditionalDataFromWorld(worldIn, pos, state);

                worldIn.setBlockState(pos, PistonAPI.hasFluidloggedAPI ? FluidloggedAPIHandler.getFluidOrAir(worldIn, pos) : Blocks.AIR.getDefaultState(), 2);
                worldIn.setBlockState(pos, Blocks.PISTON_EXTENSION.getDefaultState().withProperty(BlockPistonExtension.FACING, direction), 4);
                worldIn.setTileEntity(pos, pistonTile);

                blocksHandled[--blocksToHandle] = state.getBlock();
            }

            //handle piston head
            final BlockPos headPos = origin.offset(direction);
            if(extending) {
                final BlockPistonExtension.EnumPistonType pistonType = isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT;
                final IBlockState head = Blocks.PISTON_HEAD.getDefaultState().withProperty(BlockPistonExtension.FACING, direction).withProperty(BlockPistonExtension.TYPE, pistonType);
                final IBlockState extension = Blocks.PISTON_EXTENSION.getDefaultState().withProperty(BlockPistonMoving.FACING, direction).withProperty(BlockPistonMoving.TYPE, pistonType);

                worldIn.setBlockState(headPos, extension, 4);
                worldIn.setTileEntity(headPos, BlockPistonMoving.createTilePiston(head, direction, true, true));
            }

            //handle block updates
            for(int i = blocksToDestroy.size() - 1; i >= 0; i--) worldIn.notifyNeighborsOfStateChange(blocksToDestroy.get(i), blocksHandled[blocksToHandle++], false);
            for(int i = blocksToMove.size() - 1; i >= 0; i--) worldIn.notifyNeighborsOfStateChange(blocksToMove.get(i), blocksHandled[blocksToHandle++], false);
            if(extending) worldIn.notifyNeighborsOfStateChange(headPos, Blocks.PISTON_HEAD, false);
            return true;
        }
    }

    @SuppressWarnings("unused")
    public static final class QuarkHooks
    {
        @Nonnull
        public static EnumStickResult getStickResult(@Nonnull INonSticky block, @Nonnull IBlockSource source, @Nonnull IBlockSource other, @Nonnull IPistonInfo info) {
            final boolean canStickToBlock = block.canStickToBlock(
                    source.getWorld(),
                    info.getPistonPos(),
                    source.getBlockPos(),
                    other.getBlockPos(),
                    source.getBlockState(),
                    other.getBlockState(),
                    IStickyBehavior.getConnectingSide(source, other));

            if(!canStickToBlock) return EnumStickResult.NEVER;
            return hasStickySide(source) ? EnumStickResult.STICK : EnumStickResult.PASS;
        }

        public static boolean hasStickySide(@Nonnull IBlockSource source) {
            return source.getBlockState().getBlock().isStickyBlock(source.getBlockState());
        }
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
    public void injectData(@Nonnull Map<String, Object> data) {
        //doesn't use Loader.isModLoaded("quark") since at this point mods are not constructed
        for(String transformer : CoreModManager.getTransformers().keySet()) {
            if(transformer.startsWith("Quark Plugin")) { //quark is installed, resolve conflicts
                QuarkHandler.removeConflictingQuarkTransformers();
                break;
            }
        }
    }

    @Nullable
    @Override
    public String getAccessTransformerClass() { return null; }
}
