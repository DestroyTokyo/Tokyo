package delta.cion.cherry.server.init;

import delta.cion.cherry.server.motd.MOTDHandler;
import net.minestom.server.MinecraftServer;

public class ServerBranding {

	private static final String BRAND_NAME = "Cherry";
	private static final String MAIN_MOTD = ServerBranding.getBrandName() + " server";

	public static String getBrandName() {
		return BRAND_NAME;
	}

	public static String getMainMotd() {
		return MAIN_MOTD;
	}

	public static void setServerBranding() {
		MinecraftServer.setBrandName(BRAND_NAME);
		MOTDHandler.registerVanillaMOTD();
	}

}
