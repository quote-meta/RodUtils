package quote.fsrod.common.core.proxy;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public interface IProxy {
	public void registerEntityRenderers();
	public void registerCustomColorObjects();
	public void registerHandlers();

	@Nullable public PlayerEntity getEntityPlayerInstance();
	
	@Nullable public World getWorldClient();
	@Nullable public Iterable<ServerWorld> getWorldServers();
	@Nullable public RayTraceResult getObjectMouseOver();
	@Nullable public Entity getEntityByID(int entityID);
	@Nullable public LivingEntity getLivingByID(int livingID);
	@Nullable public PlayerEntity getPlayerByID(int playerID);
}