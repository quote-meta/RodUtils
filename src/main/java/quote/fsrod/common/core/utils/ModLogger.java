package quote.fsrod.common.core.utils;

import net.minecraft.launchwrapper.LogWrapper;
import quote.fsrod.common.core.handler.ConfigHandler;
import quote.fsrod.common.lib.LibMisc;

public class ModLogger {

    static String getModPrefix() {
        return LibMisc.MOD_NAME + ": ";
    }

    public static void warning(String format) {
        LogWrapper.warning(getModPrefix() + format);
    }

    public static void warning(String format, Object... data) {
        LogWrapper.warning(getModPrefix() + format, data);
    }

    public static void warning(Throwable exception, String format) {
        LogWrapper.warning(getModPrefix() + format);
        exception.printStackTrace();
    }
    
    public static void warning(Throwable exception, String format, Object... data) {
        LogWrapper.warning(getModPrefix() + format, data);
        exception.printStackTrace();
    }

    public static void debug(String format, Object... data) {
        if(ConfigHandler.isDebugging) {
            LogWrapper.info(getModPrefix() + format, data);

        }
    }

    public static void debug(String format) {
        if(ConfigHandler.isDebugging) {
            LogWrapper.info(getModPrefix() + format);

        }
    }
}