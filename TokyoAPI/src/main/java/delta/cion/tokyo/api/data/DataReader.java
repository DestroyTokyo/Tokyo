package delta.cion.tokyo.api.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Simple .dat file creator
 */
public class DataReader {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataReader.class);
	private final File DATA_FILE;

	public DataReader(String fileName) {
		this(new File(fileName));
	}

	public DataReader(File file) {
		this.DATA_FILE = file;
		if (!DATA_FILE.exists() || !DATA_FILE.isFile()) cannotRead();
	}



	private void cannotRead(String... text) {
		LOGGER.debug("Cannot read data file {}! {}", this.DATA_FILE, text);
		throw new RuntimeException("Cannot read data file!");
	}

}
