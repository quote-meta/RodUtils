package quote.fsrod.client.core.proxy;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import quote.fsrod.client.core.handler.InputEventHandler;
import quote.fsrod.client.core.handler.ModRenderWorldHandler;
import quote.fsrod.common.core.proxy.CommonProxy;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new ModRenderWorldHandler());
		MinecraftForge.EVENT_BUS.register(new InputEventHandler());

		registerEntityRenderers();
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		//
	}

	@Override
    public EntityPlayer getEntityPlayerInstance() {
        return Minecraft.getMinecraft().player;
	}

	@Override
	public World getWorldClient() {
		return FMLClientHandler.instance().getWorldClient();
	}

	@Override
	@Nullable
	public WorldServer[] getWorldServers(){
		return null;
	}

	@Override
	public RayTraceResult getObjectMouseOver() {
		return Minecraft.getMinecraft().objectMouseOver;
	}

	@Override
	@Nullable
	public Entity getEntityByID(int entityID){
		World world = getWorldClient();
		return world.getEntityByID(entityID);
	}
}
