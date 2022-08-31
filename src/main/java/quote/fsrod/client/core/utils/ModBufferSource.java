package quote.fsrod.client.core.utils;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.vertex.BufferBuilder;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;

public class ModBufferSource {
    private static BufferSource instance;

    public static BufferSource get(){
        if(instance == null){
            Map<RenderType, BufferBuilder> map = new HashMap<>();
            map.put(ModRenderType.CLONE_SPACE, new BufferBuilder(256));
            map.put(ModRenderType.BOX, new BufferBuilder(256));
            instance = MultiBufferSource.immediateWithBuffers(
                map,
                new BufferBuilder(256)
            );
        }
        return instance;
    }
}
