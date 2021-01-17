package quote.fsrod.common.core.proxy;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import quote.fsrod.common.core.handler.PlayerTracker;

public class CommonProxy implements IProxy {

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
	public PlayerEntity getEntityPlayerInstance() {
		return null;
	}

	@Override
	@Nullable
	public World getWorldClient() {
		return null;
	}

	@Override
	@Nullable
	public Iterable<ServerWorld> getWorldServers(){
		return ServerLifecycleHooks.getCurrentServer().getWorlds(); // AM2
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
		for (ServerWorld ws : getWorldServers()){
			ent = ws.getEntityByID(entityID);
			if (ent != null){
				if (!(ent instanceof LivingEntity)) return null;
				else break;
			}
		}
		return ent;
	}

	@Override
	@Nullable
	public LivingEntity getLivingByID(int entityID){
		Entity ent = getEntityByID(entityID);
		return ent instanceof LivingEntity ? (LivingEntity)ent : null;
	}

	@Override
	@Nullable
	public PlayerEntity getPlayerByID(int entityID){
		Entity ent = getEntityByID(entityID);
		return ent instanceof PlayerEntity ? (PlayerEntity)ent : null;
	}

}
