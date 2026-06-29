package delta.cion.tokyo.server.console;

import net.minestom.server.MinecraftServer;

public class DeExceptionManager {
	public static void init() {
		MinecraftServer.getExceptionManager().setExceptionHandler(e -> {
			if (e instanceof RuntimeException re && re.getCause() instanceof IllegalArgumentException iae
				&& "String is too long".equals(iae.getMessage())) {
				return;
			}
			if (e instanceof IllegalStateException ise && ise.getMessage() != null
				&& ise.getMessage().contains("Packet id") && ise.getMessage().contains("isn't registered!")) {
				return;
			}
			e.printStackTrace();
		});
	}
}
