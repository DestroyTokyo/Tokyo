package delta.cion.cherry.server.plugin_new.classpath;

import java.util.Objects;
import delta.cion.cherry.api.plugin_new.loader.PluginClasspathBuilder;
import xyz.endelith.plugin.loader.library.ClasspathLibrary;
import xyz.endelith.server.plugin.loader.PluginClassLoader;

public final class PluginClasspathBuilderImpl implements PluginClasspathBuilder {

    private final PluginLibraryStore libraryStore;

    public PluginClasspathBuilderImpl(PluginClassLoader classLoader) {
        this.libraryStore = new PluginLibraryStore(Objects.requireNonNull(classLoader, "class loader"));
    }

    @Override
    public PluginClasspathBuilder addLibrary(ClasspathLibrary classpathLibrary) {
        Objects.requireNonNull(classpathLibrary, "classpath library").register(this.libraryStore);
        return this;
    }
}
