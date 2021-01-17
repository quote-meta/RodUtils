package quote.fsrod.client.model;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IHasCustomModel {

    @OnlyIn(Dist.CLIENT)
    public void registerCustomModel();
}