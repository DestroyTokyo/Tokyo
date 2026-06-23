package delta.cion.tokyo.api.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * It needed for share your classes and another data to another plugins.
 * I cant create normal (paper-like) plugin system. sorry.
 * Why? Im just dont want it
 */
public class DataShare {

	private static final Map<String, Object> DATA_LIST = new ConcurrentHashMap<>();

	/**
	 * Add data if key doesnt exists
	 * @param key Data key
	 * @param object Data
	 * @return true if value success added and false if key already exists.
	 */
	public static boolean addData(String key, Object object) {
		return DATA_LIST.putIfAbsent(key, object) == null;
	}

	/**
	 * Get data
	 * @param key Data key
	 * @param type Data type
	 * @return data or null if data not exists
	 */
	public static <T> T getData(String key, Class<T> type) {
		Object obj = DATA_LIST.get(key);
		if (type.isInstance(obj)) return type.cast(obj);
		return null;
	}

	/**
	 * Get data
	 * @param key Data key
	 * @return data without class type
	 */
	public static Object getData(String key) {
		return DATA_LIST.get(key);
	}

	/**
	 * Remove data
	 * @param key Data key
	 */
	public static void removeData(String key) {
		DATA_LIST.remove(key);
	}

	/**
	 * Add data or replace exist data
	 * @param key Data key
	 * @param object Data
	 */
	public static void setData(String key, Object object) {
		DATA_LIST.put(key, object);
	}

	/**
	 * Check data exist
	 * @param key Data key
	 * @return true if data is existed and false if not
	 */
	public static boolean hasKey(String key) {
		return DATA_LIST.containsKey(key);
	}

	/**
	 * Check data type
	 * @param key Data key
	 * @param clazz Data type
	 * @return true if data similar with ur clazz and false if not
	 */
	public static boolean isType(String key, Class<?> clazz) {
		Object obj = DATA_LIST.get(key);
		return clazz.isInstance(obj);
	}
}
