package delta.cion.cherry.api.plugin_new;

import java.nio.file.Path;
import org.slf4j.Logger;
import xyz.endelith.MinecraftServer;
import xyz.endelith.event.EventManager;
import xyz.endelith.event.EventOwner;
import xyz.endelith.plugin.bootstrap.BootstrapContext;

public abstract class Plugin implements EventOwner {

    private EventManager<Plugin> eventManager;
    private MinecraftServer server;
    private PluginMetadata metadata;
    private Path dataDirectory;
    private Path sourceFile;
    private Logger logger;
    private boolean enabled;

    @Override
    public EventManager<Plugin> eventManager() {
        return this.eventManager;
    }

    public MinecraftServer server() {
        return this.server;
    }

    public PluginMetadata metadata() {
        return this.metadata;
    }

    public Path dataDirectory() {
        return this.dataDirectory;
    }

    public Path sourceFile() {
        return this.sourceFile;
    }

    public Logger logger() {
        return this.logger;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void bootstrap(BootstrapContext context) {
    }

    public void onEnable() {
    }

    public void onDisable() {
    }
}
