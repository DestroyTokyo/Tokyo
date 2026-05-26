package delta.cion.cherry.api.permission;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.network.ConnectionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PermissionManager {

	private static Map<String, ArrayList<String>> PLAYER_PERMISSIONS = new HashMap<>();

	private static final ConnectionManager CONNECTION_MANAGER = MinecraftServer.getConnectionManager();

	private static int opLevel = 4;

	public static int getOpLevel() {
		return opLevel;
	}

	public static void setOpLevel(int newOpLevel) {
		opLevel = newOpLevel;
	}

	public static void addPermission(String playerName, String permission) {
		ArrayList<String> permissions;
		if (PLAYER_PERMISSIONS.containsKey(playerName))
			permissions = PLAYER_PERMISSIONS.get(playerName);
		else permissions = new ArrayList<>();
		permissions.add(permission);
		PLAYER_PERMISSIONS.put(playerName, permissions);
		PermissionHandler.savePermissions();
	}

	public static void addPermission(Player player, String permission) {
		addPermission(player.getUsername(), permission);
	}

	public static void removePermission(String playerName, String permission) {
		ArrayList<String> permissions;
		if (!PLAYER_PERMISSIONS.containsKey(playerName)) return;
		permissions = PLAYER_PERMISSIONS.get(playerName);
		permissions.remove(permission);
		PLAYER_PERMISSIONS.put(playerName, permissions);
		PermissionHandler.savePermissions();
	}

	public static void removePermission(Player player, String permission) {
		removePermission(player.getUsername(), permission);
	}

	public static boolean hasPermission(String playerName, String permission) {
		var permissionList = PLAYER_PERMISSIONS.get(playerName);
		if (permissionList == null) return false;

		Player player = CONNECTION_MANAGER.getOnlinePlayerByUsername(playerName);
		if (permissionList.contains(permission) || permissionList.contains("*")) return true;
		if (player != null && player.getPermissionLevel() >= opLevel) return true;

		String[] perms = permission.split("\\.");
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < perms.length - 1; i++) {
			if (i > 0) builder.append(".");
			builder.append(perms[i]);
			String parentPerm = builder + ".*";
			if (permissionList.contains(parentPerm)) return true;
		}
		return false;
	}

	public static boolean hasPermission(Player player, String permission) {
		return hasPermission(player.getUsername(), permission);
	}

	public static boolean hasPermission(String player, String... permissions) {
		for (String permission : permissions)
			if (hasPermission(player, permission)) return true;
		return false;
	}

	public static boolean hasPermission(Player player, String... permissions) {
		return hasPermission(player.getUsername(), permissions);
	}

	public static void movePlayerPermissions(String oldNickName, String newNickName) {
		if (!PLAYER_PERMISSIONS.containsKey(oldNickName)) return;
		var permissions = PLAYER_PERMISSIONS.get(oldNickName);
		PLAYER_PERMISSIONS.remove(oldNickName);
		PLAYER_PERMISSIONS.put(newNickName, permissions);
	}

	public static void movePlayerPermissions(Player oldPlayer, Player newPlayer) {
		movePlayerPermissions(oldPlayer.getUsername(), newPlayer.getUsername());
		PermissionHandler.savePermissions();
	}

	public static Map<String, ArrayList<String>> getAllPermissions() {
		return PLAYER_PERMISSIONS;
	}

	public static void setPlayerPermissions(Map<String, ArrayList<String>> newPermissions) {
		PLAYER_PERMISSIONS = newPermissions;
		PermissionHandler.savePermissions();
	}
}
