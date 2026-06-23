package delta.cion.tokyo.api.locales;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class Localize {

	private static File SERVER_LOCALE_FILE;

	private static final Logger LOGGER = LoggerFactory.getLogger(Localize.class);

	private static Map<String, String> LOCALE_KEYS = null;

	private static boolean useDefault = true;

	public static void init(String localeName) {
		var server_locale_name = "locale/"+localeName+".properties";
		var server_locale_file = new File(server_locale_name);
		if (!server_locale_file.getParentFile().exists())
			server_locale_file.getParentFile().mkdirs();
		if (server_locale_file.exists()) {
			SERVER_LOCALE_FILE = server_locale_file;
			useDefault = false;
		}
		else SERVER_LOCALE_FILE = new File("locale/default.properties");
		loadLocale();
	}

	public static String getTranslate(String key) {
		if (LOCALE_KEYS == null) return key;
		return LOCALE_KEYS.get(key);
	}

	public static String getTranslate(String key, Object... objects) {
		String template = LOCALE_KEYS.get(key);
		if (template == null) return key;
		if (template.equals(key)) return key;
		return MessageFormat.format(template, objects);
	}


	private static void loadLocale() {
		if (useDefault) buildLocale();
		LOCALE_KEYS = load();
	}

	private static Map<String, String> load() {
		try (InputStream propertiesStream = new FileInputStream(SERVER_LOCALE_FILE)) {
			Properties properties = new Properties();
			properties.load(propertiesStream);

			var map = new HashMap<String, String>();
			properties.forEach((key, translate) ->
				map.put(key.toString(), Objects.requireNonNullElse(translate, key).toString()));
			return map;
		} catch (IOException e) {
			LOGGER.error("Cannot load locale file ({}). Try to check file permissions or location.", SERVER_LOCALE_FILE, e);
			return null;
		}
	}

	private static void buildLocale() {
		try {
			Map<String, String> constants = DefaultLocale.getAllTranslations();
			Map<String, String> comments = DefaultLocale.getAllComments();

			if (!SERVER_LOCALE_FILE.exists()) {
				try (FileWriter writer = new FileWriter(SERVER_LOCALE_FILE)) {
					for (String key : constants.keySet()) {
						String comment = comments.get(key);
						if (comment != null && !comment.isEmpty())
							writer.write("# " + comment + "\n");
						writer.write(key + "=" + constants.get(key) + "\n\n");
					}
				}
				LOGGER.info("Default locale created: {}", SERVER_LOCALE_FILE);
				return;
			}

			Properties existingProps = new Properties();
			try (InputStream in = new FileInputStream(SERVER_LOCALE_FILE)) {
				existingProps.load(in);
			}

			boolean added = false;
			try (FileWriter writer = new FileWriter(SERVER_LOCALE_FILE, true)) {
				for (String key : constants.keySet()) {
					if (!existingProps.containsKey(key)) {
						String comment = comments.get(key);
						if (comment != null && !comment.isEmpty())
							writer.write("\n# " + comment + "\n");
						writer.write(key + "=" + constants.get(key) + "\n");
						added = true;
					}
				}
			}

			if (added) LOGGER.debug("Locale updated. Added some new keys");
			else LOGGER.info("Locale contains all keys");
		} catch (IOException e) {
			LOGGER.error("Cannot build locale file", e);
		}
	}
}
