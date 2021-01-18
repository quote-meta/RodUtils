package quote.fsrod.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import quote.fsrod.client.core.proxy.ClientProxy;
import quote.fsrod.common.core.handler.ConfigHandler;
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
        proxy.registerHandlers();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.COMMON_SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event){
        ModPacketHandler.init();
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
