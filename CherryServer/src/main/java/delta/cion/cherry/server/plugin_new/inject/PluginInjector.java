package delta.cion.cherry.server.plugin_new.inject;

import java.lang.reflect.Field;
import java.nio.file.Path;
import org.slf4j.Logger;
import xyz.endelith.MinecraftServer;
import xyz.endelith.plugin.Plugin;
import xyz.endelith.plugin.PluginMetadata;
import xyz.endelith.server.event.EventManagerImpl;

public final class PluginInjector {

    private static final Field EVENT_MANAGER = field("eventManager");
    private static final Field SERVER = field("server");
    private static final Field METADATA = field("metadata");
    private static final Field DATA_DIRECTORY = field("dataDirectory");
    private static final Field SOURCE_FILE = field("sourceFile");
    private static final Field LOGGER = field("logger");

    private PluginInjector() {
    }

    private static Field field(String name) {
        try {
            Field field = Plugin.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static void inject(
            Plugin plugin,
            EventManagerImpl<Plugin> eventManager,
            MinecraftServer server,
            PluginMetadata metadata,
            Path dataDirectory,
            Path sourceFile,
            Logger logger
    ) throws IllegalAccessException {
        set(plugin, EVENT_MANAGER, eventManager);
        set(plugin, SERVER, server);
        set(plugin, METADATA, metadata);
        set(plugin, DATA_DIRECTORY, dataDirectory);
        set(plugin, SOURCE_FILE, sourceFile);
        set(plugin, LOGGER, logger);
    }

    private static void set(Plugin plugin, Field field, Object value) throws IllegalAccessException {
        field.set(plugin, value);
    }
}
