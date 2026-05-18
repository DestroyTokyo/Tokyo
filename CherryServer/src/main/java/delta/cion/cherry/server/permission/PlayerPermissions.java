package delta.cion.cherry.server.permission;

public interface PlayerPermissions {

	// Citory_
	String playerName();
	// msnt.stop | msnt.plugins.* | etc
	String[] permissions();
	// 0-4 (player-helper-moder-owner)
	// ex: 1 can ban 0 but cant ban 2
	int opLevel();

}
