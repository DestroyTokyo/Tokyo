package delta.cion.tokyo.api.data;

import net.minestom.server.network.NetworkBuffer.*;
import net.minestom.server.network.NetworkBuffer;


/**
 * Simple .dat file creator
 */
public class DataWriter {

	private String FILE_EXTENSION = ".dat";

	public DataWriter(String fileExtension) {
		this.FILE_EXTENSION = fileExtension;
	}

	public byte[] convertToByte(Object object) {

		return NetworkBuffer.makeArray(buffer -> {
			buffer.write(NetworkBuffer.INT, 123);
		});
	}
}
