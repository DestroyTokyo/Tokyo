package delta.cion.tokyo.api.permission;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class PermissionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(PermissionHandler.class);

	private static final File PERMISSION_FILE = new File("permissions.json");

	private static final Map<String, ArrayList<String>> ALL_PERMISSIONS = new HashMap<>();

	public static void init() {
		loadPermissions();
	}

	public static void savePermissions() {
		JSONObject root = new JSONObject();
		var perms = PermissionManager.getAllPermissions();
		for (Map.Entry<String, ArrayList<String>> entry : perms.entrySet()) {
			JSONArray permsArray = new JSONArray(entry.getValue());
			root.put(entry.getKey(), permsArray);
		}

		try (FileWriter writer = new FileWriter(PERMISSION_FILE)) {
			writer.write(root.toString(2));
		} catch (IOException e) {
			LOGGER.error("Cannot save permissions file.", e);
		}
	}

	public static void loadPermissions() {
		if (!PERMISSION_FILE.exists()) {
			PermissionManager.setPlayerPermissions(ALL_PERMISSIONS);
			return;
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(PERMISSION_FILE))) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) sb.append(line);

			JSONObject root = new JSONObject(sb.toString());
			for (Iterator<String> it = root.keys(); it.hasNext(); ) {
				String userName = it.next();
				JSONArray permsArray = root.getJSONArray(userName);

				ArrayList<String> permissions = new ArrayList<>();
				for (int i = 0; i < permsArray.length(); i++) {
					permissions.add(permsArray.getString(i));
				}
				ALL_PERMISSIONS.put(userName, permissions);
				PermissionManager.setPlayerPermissions(ALL_PERMISSIONS);
			}
		} catch (Exception e) {
			LOGGER.error("Cannot load permissions file.", e);
		}
	}

}
