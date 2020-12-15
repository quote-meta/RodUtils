package quote.fsRod.common.core.proxy;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface IProxy {
	public void preInit(FMLPreInitializationEvent event);
	public void init(FMLInitializationEvent event);
	public void postInit(FMLPostInitializationEvent event);
	
	public void registerEntityRenderers();
	public void registerCustomColorObjects();
	public void registerHandlers();

	@Nullable public EntityPlayer getEntityPlayerInstance();
	
	@Nullable public World getWorldClient();
	@Nullable public WorldServer[] getWorldServers();
	@Nullable public RayTraceResult getObjectMouseOver();
	@Nullable public Entity getEntityByID(int entityID);
	@Nullable public EntityLivingBase getLivingByID(int livingID);
	@Nullable public EntityPlayer getPlayerByID(int playerID);
}