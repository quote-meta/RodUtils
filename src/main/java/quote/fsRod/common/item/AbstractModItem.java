package quote.fsRod.common.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import quote.fsRod.client.model.IHasCustomModel;
import quote.fsRod.common.lib.LibMisc;

public abstract class AbstractModItem extends Item implements IHasCustomModel {
    
    protected AbstractModItem(String name){
        setRegistryName(new ResourceLocation(LibMisc.MOD_ID, name));
        setUnlocalizedName(name);
        setCreativeTab(CreativeTabs.TOOLS);
    }
    

    @SideOnly(Side.CLIENT)
    @Override
    public void registerCustomModel() {
        ModelLoader.setCustomModelResourceLocation(
            this, 0, new ModelResourceLocation(getRegistryName(), "inventory")
        );
    }
}