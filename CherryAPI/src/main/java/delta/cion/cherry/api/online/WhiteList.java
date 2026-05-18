package delta.cion.cherry.api.online;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.network.ConnectionManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class WhiteList {

	private static boolean ENABLE_WHITELIST = true;

	private static final File WHITELIST_FILE = new File("whitelist.json");

	private static ArrayList<PlayerInfo> WHITELIST = new ArrayList<>();

	public static boolean getStatus() {
		return ENABLE_WHITELIST;
	}

	public static void setStatus(boolean status) {
		ENABLE_WHITELIST = status;
	}

	private static final ConnectionManager CONNECTION_MANAGER = MinecraftServer.getConnectionManager();

	private static final Logger LOGGER = LoggerFactory.getLogger(WhiteList.class);

	private static void loadWhitelist(ArrayList<PlayerInfo> whitelist) {
		WHITELIST = whitelist;
	}

	public static void saveWhitelist() {
		JSONArray root = new JSONArray();
		for (PlayerInfo info : WHITELIST) {
			JSONArray entry = new JSONArray();

			if (info.name() != null) entry.put(info.name());
			else entry.put(JSONObject.NULL);
			if (info.uuid() != null) entry.put(info.uuid().toString());
			else entry.put(JSONObject.NULL);
			root.put(entry);
		}

		try (FileWriter writer = new FileWriter(WHITELIST_FILE)) {
			writer.write(root.toString(2));
		} catch (IOException e) {
			LOGGER.error("Cannot save whitelist file.", e);
		}
	}

	public static void loadWhitelistFromFile() {
		if (!WHITELIST_FILE.exists()) return;
		ArrayList<PlayerInfo> whitelist = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(WHITELIST_FILE))) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) sb.append(line);

			JSONArray root = new JSONArray(sb.toString());
			for (int i = 0; i < root.length(); i++) {
				JSONArray entry = root.getJSONArray(i);
				String name = entry.isNull(0) ? null : entry.getString(0);
				String uuidStr = entry.isNull(1) ? null : entry.getString(1);
				UUID uuid = (uuidStr != null) ? UUID.fromString(uuidStr) : null;
				whitelist.add(new PlayerInfo(name, uuid, null));
			}
		} catch (Exception e) {
			LOGGER.error("Cannot load whitelist file.", e);
			return;
		}
		loadWhitelist(whitelist);
	}

	public static void comparePlayerData() {
		compareByName();
		compareByUUID();
		WhiteList.saveWhitelist();
	}

	private static void compareByName() {
		for (PlayerInfo playerInfo : WHITELIST) {
			if (playerInfo.name().isBlank()) continue;
			Player player = CONNECTION_MANAGER.getOnlinePlayerByUsername(playerInfo.name());
			if (player == null) continue;
			WHITELIST.add(new PlayerInfo(playerInfo.name(), player.getUuid(), null));
			WHITELIST.remove(playerInfo);
		}
	}

	private static void compareByUUID() {
		for (PlayerInfo playerInfo : WHITELIST) {
			if (playerInfo.uuid() == null) continue;
			Player player = CONNECTION_MANAGER.getOnlinePlayerByUuid(playerInfo.uuid());
			if (player == null) continue;
			WHITELIST.add(new PlayerInfo(player.getUsername(), playerInfo.uuid(), null));
			WHITELIST.remove(playerInfo);
		}
	}

	private static Collection<Player> getOnlinePlayers() {
		return CONNECTION_MANAGER.getOnlinePlayers();
	}

	public static boolean isWhitelisted(String playerName) {
		for (PlayerInfo playerInfo : WHITELIST) {
			if (playerInfo.name().isBlank()) return false;
			if (playerInfo.name().equals(playerName)) return true;
		}
		return false;
	}

	public static boolean isWhitelisted(UUID playerUUID) {
		for (PlayerInfo playerInfo : WHITELIST) {
			if (playerInfo.uuid() == null) return false;
			if (playerInfo.uuid().equals(playerUUID)) return true;
		}
		return false;
	}

	public static void addToWhitelist(String playerName) {
		if (isWhitelisted(playerName)) return;
		WHITELIST.add(isPlayerOnline(playerName));
		WhiteList.saveWhitelist();
	}

	public static void addToWhitelist(UUID playerUUID) {
		if (isWhitelisted(playerUUID)) return;
		WHITELIST.add(isPlayerOnline(playerUUID));
		WhiteList.saveWhitelist();
	}

	public static void removeFromWhitelist(UUID playerUUID) {
		if (!isWhitelisted(playerUUID)) return;
		for (PlayerInfo playerInfo : WHITELIST) {
			if (playerInfo.uuid().equals(playerUUID)) {
				WHITELIST.remove(playerInfo);
				break;
			}
		}
		WhiteList.saveWhitelist();
	}

	public static void removeFromWhitelist(String playerName) {
		if (!isWhitelisted(playerName)) return;
		for (PlayerInfo playerInfo : WHITELIST) {
			if (playerInfo.name().equals(playerName)) {
				WHITELIST.remove(playerInfo);
				break;
			}
		}
		WhiteList.saveWhitelist();
	}

	public static PlayerInfo isPlayerOnline(String playerName) {
		for (Player player : getOnlinePlayers()) {
			if (player.getUsername().equals(playerName))
				return new PlayerInfo(playerName, player.getUuid(), null);
		}
		return new PlayerInfo(playerName, null, null);
	}

	public static PlayerInfo isPlayerOnline(UUID playerUUID) {
		for (Player player : getOnlinePlayers()) {
			if (player.getUuid().equals(playerUUID))
				return new PlayerInfo(player.getUsername(), playerUUID, null);
		}
		return new PlayerInfo(null, playerUUID, null);
	}
}
