package delta.cion.cherry.api.plugin_new.loader.library.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import xyz.endelith.plugin.loader.library.ClasspathLibrary;
import xyz.endelith.plugin.loader.library.LibraryLoadingException;
import xyz.endelith.plugin.loader.library.LibraryStore;

public final class JarLibrary implements ClasspathLibrary {

    private final Path path;

    public JarLibrary(Path path) {
        this.path = Objects.requireNonNull(path, "path");
    }

    @Override
    public void register(LibraryStore store) throws LibraryLoadingException {
        if (Files.notExists(this.path)) {
            throw new LibraryLoadingException("Could not find library at " + this.path);
        }

        store.addLibrary(this.path);
    }
}
