package delta.cion.cherry.api;

import java.io.InputStream;
import java.util.Properties;

public class ServerBranding {

	private static final String SERVER_VERSION;

	private static final String BRAND_NAME = "Cherry";
	private static final String MAIN_MOTD = ServerBranding.getBrandName() + " server";

	private static final String CHERRY_BRANDING_FILE = "/cherry.properties";

	static {
		String version = "unknown";
		try (InputStream is = ServerBranding.class.getResourceAsStream(CHERRY_BRANDING_FILE)) {
			if (is != null) {
				Properties props = new Properties();
				props.load(is);
				version = props.getProperty("version", "unknown");
			} else throw new RuntimeException("Cannot parse ["+CHERRY_BRANDING_FILE+"] file!");
		} catch (Exception ignored) {}
		SERVER_VERSION = version;
	}

	public static String getBrandName() {
		return BRAND_NAME;
	}

	public static String getMainMotd() {
		return MAIN_MOTD;
	}

	public static String getServerVersion() { return SERVER_VERSION; }

}
