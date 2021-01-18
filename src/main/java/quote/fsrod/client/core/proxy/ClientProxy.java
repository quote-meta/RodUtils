package quote.fsrod.client.core.proxy;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import quote.fsrod.client.core.handler.InputEventHandler;
import quote.fsrod.client.core.handler.ModRenderWorldHandler;
import quote.fsrod.common.core.handler.PlayerTracker;
import quote.fsrod.common.core.proxy.CommonProxy;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void registerHandlers() {
		MinecraftForge.EVENT_BUS.register(new InputEventHandler());
		MinecraftForge.EVENT_BUS.register(new ModRenderWorldHandler());
		MinecraftForge.EVENT_BUS.register(PlayerTracker.INSTANCE);
	}

	@Override
    public PlayerEntity getEntityPlayerInstance() {
        return Minecraft.getInstance().player;
	}

	@Override
	public World getWorldClient() {
		return LogicalSidedProvider.INSTANCE.get(LogicalSide.CLIENT);
	}

	@Override
	@Nullable
	public Iterable<ServerWorld> getWorldServers(){
		return null;
	}

	@Override
	public RayTraceResult getObjectMouseOver() {
		return Minecraft.getInstance().objectMouseOver;
	}

	@Override
	@Nullable
	public Entity getEntityByID(int entityID){
		World world = getWorldClient();
		return world.getEntityByID(entityID);
	}
}
