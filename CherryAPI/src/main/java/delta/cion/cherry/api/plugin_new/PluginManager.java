package delta.cion.cherry.api.plugin_new;

import java.util.Collection;
import org.jetbrains.annotations.Nullable;

public interface PluginManager {

    @Nullable Plugin plugin(String name);

    Collection<Plugin> plugins();
}
