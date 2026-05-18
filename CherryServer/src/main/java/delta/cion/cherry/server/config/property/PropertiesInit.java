package delta.cion.cherry.server.config.property;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.Properties;

public class PropertiesInit {

	private static final File SERVER_PROPERTIES_FILE = new File("server.properties");

	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesInit.class);

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
				if (server_properties.containsKey(key)) return;
				server_properties.setProperty(key, constants.get(key));
				all_keys = false;
			}

			if (all_keys) return;
			LOGGER.info("server.properties updated. Added some new keys");
			try (OutputStream propertiesOut = new FileOutputStream(SERVER_PROPERTIES_FILE)) {
				server_properties.store(propertiesOut, "server.properties updated");
			}
		}
	}
}
