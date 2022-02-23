package quote.fsrod.common.core.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import quote.fsrod.common.core.handler.ConfigHandler;
import quote.fsrod.common.lib.LibMisc;

public class ModLogger {

    private static final Logger LOGGER = LogManager.getLogger();

    static String getModPrefix() {
        return LibMisc.MOD_NAME + ": ";
    }

    public static void info(String format) {
        LOGGER.info(getModPrefix() + format);
    }

    public static void info(String format, Object... data) {
        LOGGER.info(getModPrefix() + format, data);
    }

    public static void warning(String format) {
        LOGGER.warn(getModPrefix() + format);
    }

    public static void warning(String format, Object... data) {
        LOGGER.warn(getModPrefix() + format, data);
    }

    public static void warning(Throwable exception, String format) {
        LOGGER.warn(getModPrefix() + format);
        exception.printStackTrace();
    }
    
    public static void warning(Throwable exception, String format, Object... data) {
        LOGGER.warn(getModPrefix() + format, data);
        exception.printStackTrace();
    }

    public static void debug(String format, Object... data) {
        if(ConfigHandler.COMMON.isDebugging.get()) {
            LOGGER.info(getModPrefix() + format, data);
        }
    }

    public static void debug(String format) {
        if(ConfigHandler.COMMON.isDebugging.get()) {
            LOGGER.info(getModPrefix() + format);

        }
    }
}