package git.jbredwards.piston_api.mod.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
@Config(modid = "piston_api")
@Mod.EventBusSubscriber(modid = "piston_api")
public final class PistonAPIConfig
{
    @Config.RangeInt(min = 1)
    @Config.LangKey("config.piston_api.maxPushLimit")
    public static int maxPushLimit = 12;

    @Config.LangKey("config.piston_api.pushFluidStates")
    public static boolean pushFluidStates = true;

    @Config.LangKey("config.piston_api.pushTileEntities")
    public static boolean pushTileEntities = true;

    @SubscribeEvent
    static void syncConfig(@Nonnull ConfigChangedEvent.OnConfigChangedEvent event) {
        if("piston_api".equals(event.getModID())) ConfigManager.sync("piston_api", Config.Type.INSTANCE);
    }
}
