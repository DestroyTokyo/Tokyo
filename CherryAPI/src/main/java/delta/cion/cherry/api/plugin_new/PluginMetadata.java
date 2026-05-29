package delta.cion.cherry.api.plugin_new;

import java.util.Collection;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Nullable;

public interface PluginMetadata {

    String mainClass();

    @Pattern("[a-zA-Z0-9_]+")
    String name();

    String version();

    @Nullable String description();

    @Nullable String loaderClass();

    Collection<String> authors();

    Collection<Dependency> dependencies();

    interface Dependency {

        @Pattern("[a-zA-Z0-9_]+")
        String name();

        boolean required();
    }
}
