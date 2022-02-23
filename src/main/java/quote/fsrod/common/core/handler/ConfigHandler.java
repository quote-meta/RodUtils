package quote.fsrod.common.core.handler;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigHandler {

    public static class Common {
        public final ForgeConfigSpec.BooleanValue isDebugging;
        public final ForgeConfigSpec.IntValue rodCloneMaxLength;
        public final ForgeConfigSpec.IntValue rodRecollectionMaxLength;
        public final ForgeConfigSpec.IntValue rodMeasurementMaxLength;
        public final ForgeConfigSpec.BooleanValue rodRecollectionInitialFreeBuild;

        public Common(ForgeConfigSpec.Builder builder) {
            isDebugging = builder
                .comment("Debug mode.")
                .define("debug", false);
            rodCloneMaxLength = builder
                .comment("Max length of selecting area for Rod of Clone/Transfer.")
                .defineInRange("rodClone.maxLength", 64, 1, Integer.MAX_VALUE);
            rodRecollectionMaxLength = builder
                .comment("Max length of selecting area for Rod of Recollection.")
                .defineInRange("rodRecollection.maxLength", 64, 1, Integer.MAX_VALUE);
            rodMeasurementMaxLength = builder
                .comment("Max num of generates Measuring Block when use Rod of Measurement.")
                .defineInRange("rodMeasurement.maxLength", 50, 1, Integer.MAX_VALUE);
            rodRecollectionInitialFreeBuild = builder
                .comment("No use.")
                .define("rodRecollectionInitialFreeBuild", true);
        }
    }

    public static final Common COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;
	static {
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = specPair.getRight();
		COMMON = specPair.getLeft();
	}
}