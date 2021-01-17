package quote.fsrod.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import quote.fsrod.client.core.proxy.ClientProxy;
import quote.fsrod.common.core.network.ModPacketHandler;
import quote.fsrod.common.core.proxy.CommonProxy;
import quote.fsrod.common.core.proxy.IProxy;
import quote.fsrod.common.lib.LibMisc;

@Mod(LibMisc.MOD_ID)
public class RodUtils {
    
    public static RodUtils instance;
    public static IProxy proxy;
    public static final Logger logger = LogManager.getLogger(LibMisc.MOD_ID);

    public RodUtils() {
        instance = this;
        proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
    }

    // @EventHandler
    // public void preInit(FMLPreInitializationEvent event){
    //     RodUtils.logger = event.getModLog();

    //     proxy.preInit(event);
    //     proxy.registerHandlers();
    //     proxy.registerEntityRenderers();

    // }

    // @EventHandler
    // public void init(FMLInitializationEvent event){
    //     ModPacketHandler.init();

    //     proxy.init(event);
    //     proxy.registerCustomColorObjects();
    // }
}
