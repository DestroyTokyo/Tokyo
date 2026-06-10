package delta.cion.cherry.api.locales;

import java.util.LinkedHashMap;
import java.util.Map;

public final class DefaultLocale {

	private DefaultLocale() {}

	private static final Map<String, String> TRANSLATIONS = new LinkedHashMap<>();

	private static final Map<String, String> COMMENTS = new LinkedHashMap<>();

	static {
		addEntry("no-permission", "You dont have permissions to /{0}", "Player tried to use command what he hasn't permissions");
		addEntry("not-whitelisted", "Sorry, {0}, but you cannot connect to this server.", "Unwhitelisted player tried to join");
		addEntry("server-closed", "Server closed\\n{0}", "Server closed message with timestamp");
	}

	private static void addEntry(String key, String translation, String comment) {
		TRANSLATIONS.put(key, translation);
		if (comment != null && !comment.isEmpty()) COMMENTS.put(key, comment);
	}

	public static Map<String, String> getAllTranslations() {
		return TRANSLATIONS;
	}

	public static Map<String, String> getAllComments() {
		return COMMENTS;
	}

}
