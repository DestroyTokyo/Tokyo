package delta.cion.cherry.server.plugin_new.loader;

import xyz.endelith.plugin.Plugin;
import xyz.endelith.plugin.PluginMetadata.Dependency;

public final class UnknownDependencyException extends RuntimeException {

    private final String pluginName;
    private final String dependencyName;

    public UnknownDependencyException(Plugin plugin, Dependency dependency) {
        super(String.format(
                "Plugin %s requires unknown dependency %s",
                plugin.metadata().name(),
                dependency.name()
        ));

        this.pluginName = plugin.metadata().name();
        this.dependencyName = dependency.name();
    }

    public String pluginName() {
        return this.pluginName;
    }

    public String dependencyName() {
        return this.dependencyName;
    }
}
