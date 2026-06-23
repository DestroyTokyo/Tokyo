package delta.cion.tokyo.server.config.property;

import java.util.HashMap;
import java.util.Map;

public class PropertyConstants {
	private static final Map<String, String> config = new HashMap<>();

	public static Map<String, String> getConfig() {
		return setConfig();
	}

	private static Map<String, String> setConfig() {
		// Server connection
		config.put("open-lan", "false");
		config.put("server-ip", "0.0.0.0");
		config.put("server-port", "25565");
		config.put("online-mode", "false");

		// Server locale
		config.put("server-locale", "default");

		// Debug messages etc
		config.put("debug-mode", "false");

		// WhiteList
		config.put("enable-whitelist", "false");
		config.put("whitelist-mode", "uuid");

		// BanList
		config.put("enable-banlist", "false");
		config.put("banlist-mode", "uuid"); // uuid, name, ip

		return config;
	}
}
