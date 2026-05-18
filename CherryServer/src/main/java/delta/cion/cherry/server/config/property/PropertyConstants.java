package delta.cion.cherry.server.config.property;

import java.util.HashMap;
import java.util.Map;

public class PropertyConstants {
	private static final Map<String, String> config = new HashMap<>();

	public static Map<String, String> getConfig() {
		return setConfig();
	}

	private static Map<String, String> setConfig() {
		config.put("server-ip", "0.0.0.0");
		config.put("server-port", "25565");
		config.put("debug", "false");
		config.put("online-mode", "false");

		return config;
	}
}
