package quote.fsrod.common;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import quote.fsrod.common.core.network.ModPacketHandler;
import quote.fsrod.common.core.proxy.IProxy;
import quote.fsrod.common.lib.LibMisc;

@Mod(modid = LibMisc.MOD_ID, name = LibMisc.MOD_NAME, version = LibMisc.VERSION)
public class RodUtils {

    @SidedProxy(clientSide = LibMisc.PROXY_CLIENT, serverSide = LibMisc.PROXY_COMMON)
    public static IProxy proxy;
    
    @Instance
    public static RodUtils instance;

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        RodUtils.logger = event.getModLog();

        proxy.preInit(event);
        proxy.registerHandlers();
        proxy.registerEntityRenderers();

    }

    @EventHandler
    public void init(FMLInitializationEvent event){
        ModPacketHandler.init();

        proxy.init(event);
        proxy.registerCustomColorObjects();
    }
}
