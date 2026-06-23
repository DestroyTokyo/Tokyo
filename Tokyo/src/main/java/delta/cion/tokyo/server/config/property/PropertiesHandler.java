package delta.cion.tokyo.server.config.property;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.Properties;

public class PropertiesHandler {

	private static final File SERVER_PROPERTIES_FILE = new File("server.properties");

	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesHandler.class);

	public static void buildConfig() throws IOException {
		if (!SERVER_PROPERTIES_FILE.exists()) {
			if (SERVER_PROPERTIES_FILE.createNewFile()) LOGGER.info("server.properties file created");
			else throw new IOException("Cannot create server.properties file");
		};
		try (InputStream propertiesStream = new FileInputStream(SERVER_PROPERTIES_FILE)) {
			Properties server_properties = new Properties();
			server_properties.load(propertiesStream);
			Map<String, String> constants = PropertyConstants.getConfig();
			boolean all_keys = true;
			for (String key : constants.keySet()) {
				//LOGGER.debug("Checking {} in the server.properties", key);
				if (server_properties.containsKey(key)) {
				//	LOGGER.debug("server.properties contains {}. Skipping...", key);
					continue;
				}
				server_properties.setProperty(key, constants.get(key));
				all_keys = false;
			}

			if (all_keys) {
				LOGGER.info("server.properties contains all needed variables");
				return;
			} else LOGGER.debug("server.properties updating. Adding some new keys...");
			try (OutputStream propertiesOut = new FileOutputStream(SERVER_PROPERTIES_FILE)) {
				server_properties.store(propertiesOut, "server.properties updated");
			}
		}
	}

	public static Properties getProperties(String propertiesPath) {
		File propertiesFile = new File(propertiesPath);
		if (!propertiesFile.exists()) {
			LOGGER.error("Cannot found {}, try to check file permissions or location", propertiesPath);
			return null;
		}

		try (InputStream propertiesStream = new FileInputStream(SERVER_PROPERTIES_FILE)) {
			Properties properties = new Properties();
			properties.load(propertiesStream);
			return properties;
		} catch (IOException e) {
			LOGGER.error("Cannot load properties file ({}). Try to check file permissions or location.", propertiesPath, e);
			return null;
		}
	}
}
