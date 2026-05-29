package delta.cion.cherry.server.plugin_new;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurateException;
import xyz.endelith.plugin.Plugin;
import xyz.endelith.plugin.PluginManager;
import xyz.endelith.plugin.loader.PluginLoader;
import xyz.endelith.plugin.loader.library.LibraryLoadingException;
import xyz.endelith.server.MinecraftServerImpl;
import xyz.endelith.server.plugin.bootstrap.BootstrapContextImpl;
import xyz.endelith.server.plugin.classpath.PluginClasspathBuilderImpl;
import xyz.endelith.server.plugin.inject.PluginInjector;
import xyz.endelith.server.plugin.loader.CircularDependencyException;
import xyz.endelith.server.plugin.loader.LoadOrderResolver;
import xyz.endelith.server.plugin.loader.PluginClassLoader;
import xyz.endelith.server.plugin.loader.UnknownDependencyException;

public final class PluginManagerImpl implements PluginManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginManagerImpl.class);

    private final Map<String, Plugin> pluginMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final File pluginsDirectory;
    private final MinecraftServerImpl server;
    private final LoadOrderResolver loadOrderResolver;

    public PluginManagerImpl(MinecraftServerImpl server) {
        this.server = Objects.requireNonNull(server, "server");
        this.pluginsDirectory = new File("plugins").getAbsoluteFile();
        this.pluginsDirectory.mkdirs();
        this.loadOrderResolver = new LoadOrderResolver(this);
        preloadPlugins();
        bootstrapPlugins();
    }

    @Override
    public @Nullable Plugin plugin(String name) {
        return this.pluginMap.get(name);
    }

    @Override
    public Collection<Plugin> plugins() {
        return List.copyOf(this.pluginMap.values());
    }

    private void preloadPlugins() {
        Collection<File> plugins = listPluginFiles(this.pluginsDirectory, new String[] {"jar"}, false);
        LOGGER.info("Found {} server plugins...", plugins.size());

        plugins.forEach(file -> {
            try {
                PluginClassLoader classLoader = new PluginClassLoader(file);

                try (InputStream stream = classLoader.getResourceAsStream("endelith.yml")) {
                    PluginMetadataImpl metadata = new PluginMetadataImpl(
                            new BufferedReader(new InputStreamReader(Objects.requireNonNull(stream)))
                    );

                    if (this.pluginMap.containsKey(metadata.name())) {
                        LOGGER.error(
                                "Cannot load {}, because plugin with name {} already exists",
                                file.getName(),
                                metadata.name()
                        );
                        return;
                    }

                    Plugin plugin = createPluginInstance(classLoader, metadata);
                    if (plugin == null) {
                        return;
                    }

                    Path dataDirectory = this.pluginsDirectory.toPath().resolve(metadata.name());
                    PluginInjector.inject(
                            plugin,
                            this.server.pluginEventManager(),
                            this.server,
                            metadata,
                            dataDirectory,
                            file.toPath(),
                            LoggerFactory.getLogger(metadata.name())
                    );

                    this.pluginMap.put(metadata.name(), plugin);
                }
            } catch (ConfigurateException ex) {
                LOGGER.atError()
                        .setCause(ex)
                        .log("Cannot load plugin {}, because of invalid plugin meta file", file.getName());
            } catch (IllegalAccessException ex) {
                LOGGER.atError()
                        .setCause(ex)
                        .log("Cannot load plugin {}, failed to inject required objects", file.getName());
            } catch (LibraryLoadingException ex) {
                LOGGER.atError()
                        .setCause(ex)
                        .log("Cannot load plugin {}, because plugin loader failed", file.getName());
            } catch (IOException ex) {
                LOGGER.atError()
                        .setCause(ex)
                        .log("Cannot load plugin {}, because plugin source could not be read", file.getName());
            } catch (NullPointerException ex) {
                LOGGER.atError()
                        .setCause(ex)
                        .log("Cannot load plugin {}, because endelith.yml does not exist", file.getName());
            }
        });

        try {
            this.loadOrderResolver.resolve();
        } catch (UnknownDependencyException | CircularDependencyException ex) {
            LOGGER.error("##################################################");
            LOGGER.error("");
            LOGGER.error(ex.getMessage());
            LOGGER.error("");
            LOGGER.error("##################################################");
        }
    }

    private void bootstrapPlugins() {
        this.loadOrderResolver.loadOrder().forEach(plugin -> {
            try {
                plugin.bootstrap(new BootstrapContextImpl(this.server.bootstrapEventManager()));
            } catch (Throwable t) {
                plugin.logger().atError()
                        .setCause(t)
                        .log("Failed to bootstrap {}", plugin.metadata().name());
            }
        });
    }

    public void enablePlugins() {
        this.loadOrderResolver.loadOrder().forEach(this::enablePlugin);
    }

    public void disablePlugins() {
        this.loadOrderResolver.loadOrder().reversed().forEach(this::disablePlugin);
    }

    private void enablePlugin(Plugin plugin) {
        try {
            plugin.logger().info("Enabling...");
            plugin.setEnabled(true);
        } catch (Throwable t) {
            plugin.logger().atError()
                    .setCause(t)
                    .log("Failed to enable {}", plugin.metadata().name());
            disablePlugin(plugin);
        }
    }

    private void disablePlugin(Plugin plugin) {
        try {
            plugin.logger().info("Disabling...");
            plugin.setEnabled(false);
        } catch (Throwable t) {
            plugin.logger().atError()
                    .setCause(t)
                    .log("Failed to disable {}", plugin.metadata().name());
        }
    }

    private Plugin createPluginInstance(PluginClassLoader classLoader, PluginMetadataImpl metadata) {
        runPluginLoader(classLoader, metadata);

        String mainClass = metadata.mainClass();

        try {
            Class<?> clazz = classLoader.loadClass(mainClass);

            if (!Plugin.class.isAssignableFrom(clazz)) {
                throw new IllegalArgumentException(clazz + " does not extend Plugin");
            }

            return (Plugin) clazz.getConstructor().newInstance();
        } catch (ReflectiveOperationException | IllegalArgumentException ex) {
            LOGGER.atError()
                    .setCause(ex)
                    .log("Failed to create an instance of {}", mainClass);
            return null;
        }
    }

    private void runPluginLoader(PluginClassLoader classLoader, PluginMetadataImpl metadata) {
        String loaderClassName = metadata.loaderClass();
        if (loaderClassName == null || loaderClassName.isBlank()) {
            return;
        }

        try {
            Class<?> loaderClass = classLoader.loadClass(loaderClassName);
            if (!PluginLoader.class.isAssignableFrom(loaderClass)) {
                throw new IllegalArgumentException(loaderClass + " does not implement PluginLoader");
            }

            PluginLoader pluginLoader = (PluginLoader) loaderClass.getConstructor().newInstance();
            pluginLoader.classloader(new PluginClasspathBuilderImpl(classLoader));
        } catch (ReflectiveOperationException | IllegalArgumentException ex) {
            throw new LibraryLoadingException("Failed to initialize plugin loader " + loaderClassName, ex);
        } catch (Exception ex) {
            throw new LibraryLoadingException("Failed to initialize plugin loader " + loaderClassName, ex);
        }
    }

    private Collection<File> listPluginFiles(File directory, String[] extensions, boolean recursive) {
        try (var stream = recursive ? Files.walk(directory.toPath()) : Files.list(directory.toPath())) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> hasExtension(path, extensions))
                    .map(Path::toFile)
                    .toList();
        } catch (IOException ex) {
            return List.of();
        }
    }

    private boolean hasExtension(Path path, String[] extensions) {
        String fileName = path.getFileName().toString();

        for (String extension : extensions) {
            if (fileName.endsWith("." + extension)) {
                return true;
            }
        }

        return false;
    }
}
