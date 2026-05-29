package delta.cion.cherry.server.plugin_new.loader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import xyz.endelith.server.MinecraftServerImpl;

public final class PluginClassLoader extends URLClassLoader {

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public PluginClassLoader(File pluginSource) throws IOException {
        super(
                new URL[] {pluginSource.toURI().toURL()},
                MinecraftServerImpl.class.getClassLoader()
        );
    }

    public void addPath(Path path) throws IOException {
        addURL(path.toUri().toURL());
    }
}
