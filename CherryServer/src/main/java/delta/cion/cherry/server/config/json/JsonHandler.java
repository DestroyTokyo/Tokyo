package delta.cion.cherry.server.config.json;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JsonHandler implements JsonConfig {

	private final String CONFIG_PATH;
	private JSONObject JSON_DATA;

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonHandler.class);

	public JsonHandler(String configPath) {
		this.CONFIG_PATH = configPath;
		readJson();
	}

	private void readJson() {
		try (FileReader reader = new FileReader(path())) {
			JSONTokener jsonTokener = new JSONTokener(reader);
			this.JSON_DATA = new JSONObject(jsonTokener);
		} catch (IOException exception) {
			LOGGER.warn("Cannot found or read: {}. Check the file path or permissions.", path());
			File jsonFile = new File(path());
			this.JSON_DATA = new JSONObject();
			if (!jsonFile.exists()) {
				if (jsonFile.getParentFile().mkdirs())
					this.writeJson();
			}
		}
	}

	public Object getData(String path) {
		if (path == null || path.isEmpty()) return this.JSON_DATA;
		if (this.JSON_DATA == null) {
			LOGGER.warn("Cannot read: {}. File is empty. Try to add some data into it.", path());
			return null;
		}

		String[] keys = path.split("\\.");
		Object object = this.JSON_DATA;

		for (String key : keys) {
			if (object instanceof JSONObject) {
				object = ((JSONObject) object).opt(key);
				if (object == null) {
					LOGGER.warn("Cannot found {} in the {}", key, path);
					return null;
				}
			} else if (object instanceof JSONArray) {
				try {
					int index = Integer.parseInt(key);
					JSONArray arr = (JSONArray) object;
					if (index < 0 || index >= arr.length()) {
						LOGGER.warn("Cannot get value with index {} from {}", index, path);
						return null;
					}
					object = arr.get(index);
				} catch (NumberFormatException e) {
					LOGGER.warn("I dont know what is it error. But u can debug it urself (JsonHandler:66)");
					return null;
				}
			} else {
				LOGGER.warn("Wrong key: {}. Maybe it's a List?", key);
				return null;
			}
		}
		return object;
	}

	public String getString(String path) {
		return (String) getData(path);
	}

	public int getInt(String path) {
		return (Integer) getData(path);
	}

	public boolean getBoolean(String path) {
		return (Boolean) getData(path);
	}

	public String[] getStringList(String path) {
		return (String[]) getData(path);
	}

	public int[] getIntList(String path) {
		return (int[]) getData(path);
	}

	public void writeJson() {
		try (FileWriter fileWriter = new FileWriter(path())) {
			fileWriter.write(this.JSON_DATA.toString(4));
		} catch (Exception exception) {
			LOGGER.error("Cannot write json to: {}. Check the file permissions.", path());
		}
	}

	@Override
	public String path() {
		return CONFIG_PATH;
	}

	@Override
	public JSONObject json() {
		return JSON_DATA;
	}
}
