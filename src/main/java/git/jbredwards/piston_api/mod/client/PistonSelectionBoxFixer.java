package git.jbredwards.piston_api.mod.client;

import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
@Mod.EventBusSubscriber(modid = "piston_api", value = Side.CLIENT)
public final class PistonSelectionBoxFixer
{
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    static void fixPistonHeadSelectionBox(@Nonnull DrawBlockHighlightEvent event) {
        //draw both parts of the piston head's bounding box
        if(event.getTarget().typeOfHit == RayTraceResult.Type.BLOCK) {
            final BlockPos pos = event.getTarget().getBlockPos();
            final IBlockState state = event.getPlayer().world.getBlockState(pos);

            //fix for when the head is selected
            if(state.getBlock() instanceof BlockPistonExtension) {
                applyFix(event.getPlayer(), pos, state, event.getPartialTicks());
                event.setCanceled(true);
            }

            //fix for when the base is selected
            if(state.getBlock() instanceof BlockPistonBase && state.getValue(BlockPistonBase.EXTENDED)) {
                final BlockPos offset = pos.offset(state.getValue(BlockPistonBase.FACING));
                final IBlockState neighbor = event.getPlayer().world.getBlockState(offset);
                if(neighbor.getBlock() instanceof BlockPistonExtension) {
                    applyFix(event.getPlayer(), offset, neighbor, event.getPartialTicks());
                    event.setCanceled(true);
                }
            }
        }
    }

    static void applyFix(@Nonnull EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, float partialTicks) {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(2);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        final double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        final double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        final double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        RenderGlobal.drawSelectionBoundingBox(state.getSelectedBoundingBox(player.world, pos).offset(-x, -y, -z).grow(0.002), 0, 0, 0, 0.4f);
        RenderGlobal.drawSelectionBoundingBox(((BlockPistonExtension)state.getBlock()).getArmShape(state).offset(pos).offset(-x, -y, -z).grow(0.002), 0, 0, 0, 0.4f);

        final BlockPos offset = pos.offset(state.getValue(BlockPistonExtension.FACING).getOpposite());
        final IBlockState neighbor = player.world.getBlockState(offset);
        if(neighbor.getBlock() instanceof BlockPistonBase) //check for piston base here, otherwise using piston heads for stuff like tables results in weirdness
            RenderGlobal.drawSelectionBoundingBox(neighbor.getSelectedBoundingBox(player.world, offset).offset(-x, -y, -z).grow(0.002), 0, 0, 0, 0.4f);

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}
