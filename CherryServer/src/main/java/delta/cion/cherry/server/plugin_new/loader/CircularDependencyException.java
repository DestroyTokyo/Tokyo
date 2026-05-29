package delta.cion.cherry.server.plugin_new.loader;

import java.util.List;
import xyz.endelith.plugin.Plugin;

public final class CircularDependencyException extends RuntimeException {

    private final List<Plugin> cycle;

    public CircularDependencyException(List<Plugin> cycle) {
        super(String.format(
                "Circular plugin dependency detected: %s",
                createMessage(cycle)
        ));

        this.cycle = List.copyOf(cycle);
    }

    public List<Plugin> cycle() {
        return this.cycle;
    }

    private static String createMessage(List<Plugin> cycle) {
        return String.join(
                " -> ",
                cycle.stream()
                        .map(plugin -> plugin.metadata().name())
                        .toList()
        );
    }
}
