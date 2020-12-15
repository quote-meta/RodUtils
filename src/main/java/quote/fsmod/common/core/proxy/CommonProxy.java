package quote.fsrod.common.core.proxy;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.server.FMLServerHandler;
import quote.fsrod.common.core.handler.ConfigHandler;
import quote.fsrod.common.core.handler.PlayerTracker;

public class CommonProxy implements IProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		ConfigHandler.INSTANCE.load(event.getSuggestedConfigurationFile());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		//
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		//
	}

	@Override
	public void registerEntityRenderers() {
		// no-op
	}

	@Override
	public void registerCustomColorObjects() {
		// no-op
	}

	@Override
	public void registerHandlers() {
		MinecraftForge.EVENT_BUS.register(PlayerTracker.INSTANCE);
	}

	@Override
	@Nullable
	public EntityPlayer getEntityPlayerInstance() {
		return null;
	}

	@Override
	@Nullable
	public World getWorldClient() {
		return null;
	}

	@Override
	@Nullable
	public WorldServer[] getWorldServers(){
		return FMLServerHandler.instance().getServer().worlds; // AM2
	}

	@Override
	@Nullable
	public RayTraceResult getObjectMouseOver() {
		return null;
	}

	@Override
	@Nullable
	public Entity getEntityByID(int entityID){
		Entity ent = null;
		for (WorldServer ws : getWorldServers()){
			ent = ws.getEntityByID(entityID);
			if (ent != null){
				if (!(ent instanceof EntityLivingBase)) return null;
				else break;
			}
		}
		return (EntityLivingBase)ent;
	}

	@Override
	@Nullable
	public EntityLivingBase getLivingByID(int entityID){
		Entity ent = getEntityByID(entityID);
		return ent instanceof EntityLivingBase ? (EntityLivingBase)ent : null;
	}

	@Override
	@Nullable
	public EntityPlayer getPlayerByID(int entityID){
		Entity ent = getEntityByID(entityID);
		return ent instanceof EntityPlayer ? (EntityPlayer)ent : null;
	}

}
