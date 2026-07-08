package delta.cion.tokyo.api.data;

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
			buffer.write(INT, PolarWorld.MAGIC_NUMBER);
			buffer.write(SHORT, PolarWorld.VERSION_IMPROVED_LIGHT);
			buffer.write(VAR_INT, dataConverter.dataVersion());
			buffer.write(BYTE, (byte) world.compression().ordinal());
			switch (world.compression()) {
				case NONE -> {
					buffer.write(VAR_INT, contentBytes.length);
					buffer.write(RAW_BYTES, contentBytes);
				}
				case ZSTD -> {
					buffer.write(VAR_INT, contentBytes.length);
					buffer.write(RAW_BYTES, Zstd.compress(contentBytes));
				}
			}
		});

}
