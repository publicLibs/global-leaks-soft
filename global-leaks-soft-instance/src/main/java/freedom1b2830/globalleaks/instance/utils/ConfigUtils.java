package freedom1b2830.globalleaks.instance.utils;

import java.io.File;
import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import wcas.freedom1b2830.globalleaks.config.GlobalLeakConfig;

public final class ConfigUtils {
	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(new YAMLFactory());

	public static GlobalLeakConfig readConfig(final @Nullable GlobalLeakConfig config, final @NotNull File configFile)
			throws IOException {

		GlobalLeakConfig result;
		if (configFile.exists()) {
			result = OBJECT_MAPPER.readValue(configFile, GlobalLeakConfig.class);
			return result;
		}
		if (config == null) {
			result = new GlobalLeakConfig();
			OBJECT_MAPPER.writeValue(configFile, result);
			return result;
		}
		OBJECT_MAPPER.writeValue(configFile, config);
		return config;
	}

	private ConfigUtils() {

	}

}
