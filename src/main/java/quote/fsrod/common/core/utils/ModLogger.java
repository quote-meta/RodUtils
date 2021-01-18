package quote.fsrod.common.core.utils;

import quote.fsrod.common.RodUtils;
import quote.fsrod.common.core.handler.ConfigHandler;
import quote.fsrod.common.lib.LibMisc;

public class ModLogger {
    private ModLogger(){}

    static String getModPrefix() {
        return LibMisc.MOD_NAME + ": ";
    }

    public static void warning(String format) {
        RodUtils.logger.warn(getModPrefix(), format);
    }

    public static void warning(String format, Object... data) {
        RodUtils.logger.warn(getModPrefix(), format, data);
    }

    public static void warning(Throwable exception, String format) {
        RodUtils.logger.warn(getModPrefix(), format);
        exception.printStackTrace();
    }
    
    public static void warning(Throwable exception, String format, Object... data) {
        RodUtils.logger.warn(getModPrefix(), format, data);
        exception.printStackTrace();
    }

    public static void debug(String format, Object... data) {
        if(ConfigHandler.COMMON.isDebugging.get()) {
            RodUtils.logger.info(getModPrefix(), format, data);
        }
    }

    public static void debug(String format) {
        if(ConfigHandler.COMMON.isDebugging.get()) {
            RodUtils.logger.info(getModPrefix(), format);
        }
    }
}