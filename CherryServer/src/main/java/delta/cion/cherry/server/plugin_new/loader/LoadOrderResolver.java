package delta.cion.cherry.server.plugin_new.loader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import xyz.endelith.plugin.Plugin;
import xyz.endelith.plugin.PluginMetadata.Dependency;
import xyz.endelith.server.plugin.PluginManagerImpl;

public final class LoadOrderResolver {

    private final PluginManagerImpl pluginManager;
    private List<Plugin> loadOrder = List.of();

    public LoadOrderResolver(PluginManagerImpl pluginManager) {
        this.pluginManager = Objects.requireNonNull(pluginManager, "plugin manager");
    }

    public List<Plugin> loadOrder() {
        return this.loadOrder;
    }

    public void resolve() {
        Set<Plugin> visited = new HashSet<>();
        List<Plugin> dependencyStack = new ArrayList<>();
        List<Plugin> resolvedLoadOrder = new ArrayList<>();

        for (Plugin plugin : this.pluginManager.plugins()) {
            if (!visited.contains(plugin)) {
                processPlugin(plugin, visited, dependencyStack, resolvedLoadOrder);
            }
        }

        this.loadOrder = List.copyOf(resolvedLoadOrder);
    }

    private void processPlugin(
            Plugin plugin,
            Set<Plugin> visited,
            List<Plugin> dependencyStack,
            List<Plugin> loadOrder
    ) {
        if (visited.contains(plugin)) {
            return;
        }

        if (dependencyStack.contains(plugin)) {
            throw new CircularDependencyException(createCycle(plugin, dependencyStack));
        }

        dependencyStack.add(plugin);

        for (Dependency dependency : plugin.metadata().dependencies()) {
            Plugin dependencyPlugin = this.pluginManager.plugin(dependency.name());

            if (dependencyPlugin == null) {
                if (dependency.required()) {
                    throw new UnknownDependencyException(plugin, dependency);
                }

                continue;
            }

            processPlugin(dependencyPlugin, visited, dependencyStack, loadOrder);
        }

        dependencyStack.remove(dependencyStack.size() - 1);
        visited.add(plugin);
        loadOrder.add(plugin);
    }

    private List<Plugin> createCycle(Plugin plugin, List<Plugin> dependencyStack) {
        int index = dependencyStack.indexOf(plugin);
        List<Plugin> cycle = new ArrayList<>(dependencyStack.subList(index, dependencyStack.size()));

        cycle.add(plugin);
        return List.copyOf(cycle);
    }
}
