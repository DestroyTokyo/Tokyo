package delta.cion.tokyo.api.data;

/**
 * Base data offsets
 * <br> For version, magic and index
 * <br> MAGIC 	- 5 - 0
 * <br> VERSION - 3 - 5
 * <br> INDEX   - 5 - 8
 * <br> DATA    - X - 13
 */
public enum BaseDataOffsets {
	MAGIC(0),
	VERSION(5),
	INDEX(8),
	DATA(13);

	// TOKYO - 5 - 0
	// 123 	 - 3 - 5
	// WORLD - 5 - 8
	// DATA  - X - 13

	public final int offset;

	BaseDataOffsets(int offset) {
		this.offset = offset;
	}
}
