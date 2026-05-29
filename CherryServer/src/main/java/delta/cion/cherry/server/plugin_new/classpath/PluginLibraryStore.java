package delta.cion.cherry.server.plugin_new.classpath;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import xyz.endelith.plugin.loader.library.LibraryLoadingException;
import xyz.endelith.plugin.loader.library.LibraryStore;
import xyz.endelith.server.plugin.loader.PluginClassLoader;

public final class PluginLibraryStore implements LibraryStore {

    private final PluginClassLoader classLoader;

    public PluginLibraryStore(PluginClassLoader classLoader) {
        this.classLoader = Objects.requireNonNull(classLoader, "class loader");
    }

    @Override
    public void addLibrary(Path library) {
        try {
            this.classLoader.addPath(library);
        } catch (IOException ex) {
            throw new LibraryLoadingException("Failed to add library to plugin classpath: " + library, ex);
        }
    }
}
