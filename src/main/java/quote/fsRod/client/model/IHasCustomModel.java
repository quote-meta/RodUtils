package quote.fsRod.client.model;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IHasCustomModel {

    @SideOnly(Side.CLIENT)
    public void registerCustomModel();
}