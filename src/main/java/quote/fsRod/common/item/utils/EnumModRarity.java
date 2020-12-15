package quote.fsRod.common.item.utils;

import net.minecraft.util.text.TextFormatting;

public enum EnumModRarity implements net.minecraftforge.common.IRarity {
    LEGENDARY(TextFormatting.GOLD, "Legendary");

    public final TextFormatting rarityColor;
    public final String rarityName;

    private EnumModRarity(TextFormatting color, String name){
        this.rarityColor = color;
        this.rarityName = name;
    }

    @Override
    public TextFormatting getColor(){
        return this.rarityColor;
    }

    @Override
    public String getName(){
        return this.rarityName;
    } 
}