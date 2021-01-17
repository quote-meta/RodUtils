package quote.fsrod.common.core.handler;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import quote.fsrod.common.lib.LibMisc;

public class ConfigHandler {
    public static final ConfigHandler INSTANCE = new ConfigHandler();

    public static Configuration config;

    String fileName = LibMisc.MOD_ID + ".cfg";

    public static boolean isDebugging = false;
    public static int rodCloneMaxLength = 64;
    public static int rodReincarnationMaxLength = 64;
    public static int rodMeasurementMaxLength = 50;
    public static boolean rodReincarnationInitialFreeBuild = true;
    
    public void load(File file){
        config = new Configuration(file);

        isDebugging = loadPropBool("debug", isDebugging);
        rodCloneMaxLength = loadPropInt("rodClone.maxLength", rodCloneMaxLength);
        rodReincarnationMaxLength = loadPropInt("rodReincarnation.maxLength", rodCloneMaxLength);
        rodMeasurementMaxLength = loadPropInt("rodMeasurement.maxLength", rodMeasurementMaxLength);

        if(config.hasChanged()) config.save();
    }

    public boolean loadPropBool(String key, boolean _default){
        Property prop = config.get(Configuration.CATEGORY_GENERAL, key, _default);
        return prop.getBoolean();
    }

    public int loadPropInt(String key, int _default){
        Property prop = config.get(Configuration.CATEGORY_GENERAL, key, _default);
        return prop.getInt();
    }

    public double loadPropDouble(String key, double _default){
        Property prop = config.get(Configuration.CATEGORY_GENERAL, key, _default);
        return prop.getDouble();
    }
}