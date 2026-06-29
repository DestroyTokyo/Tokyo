package delta.cion.tokyo.server.console;

import net.minestom.server.MinecraftServer;

public class DeExceptionManager {
	public static void init() {
		MinecraftServer.getExceptionManager().setExceptionHandler(e -> {
			if (e instanceof RuntimeException runtimeException) {
				Throwable cause = runtimeException.getCause();
				if (cause instanceof IllegalArgumentException &&
					cause.getMessage() != null &&
					cause.getMessage().contains("String is too long")) {
					return;
				}
			}
			e.printStackTrace();
		});
	}
}
