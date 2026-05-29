package delta.cion.cherry.server.plugin_new;

import java.io.BufferedReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import xyz.endelith.plugin.PluginMetadata;

public final class PluginMetadataImpl implements PluginMetadata {

    private static final Pattern PLUGIN_NAME_PATTERN = Pattern.compile("[a-zA-Z0-9_]+");

    private final String mainClass;
    private final String name;
    private final String version;
    private final @Nullable String description;
    private final @Nullable String loaderClass;
    private final Collection<String> authors;
    private final Collection<Dependency> dependencies;

    public PluginMetadataImpl(BufferedReader reader) throws ConfigurateException {
        CommentedConfigurationNode node = YamlConfigurationLoader.builder()
                .source(() -> reader)
                .defaultOptions(options -> options.serializers(builder -> builder
                        .register(Dependency.class, DependencySerializer.INSTANCE)
                ))
                .build()
                .load();

        this.mainClass = Objects.requireNonNull(node.node("main-class").getString(), "main-class cannot be null");
        this.name = validatePluginName(
                Objects.requireNonNull(node.node("name").getString(), "name cannot be null")
        );
        this.version = Objects.requireNonNull(node.node("version").getString(), "version cannot be null");
        this.description = node.node("description").getString();
        this.loaderClass = node.node("loader-class").getString();
        this.authors = node.node("authors").getList(String.class, List.of());
        this.dependencies = node.node("dependencies").getList(Dependency.class, List.of());
    }

    @Override
    public String mainClass() {
        return this.mainClass;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String version() {
        return this.version;
    }

    @Override
    public @Nullable String description() {
        return this.description;
    }

    @Override
    public @Nullable String loaderClass() {
        return this.loaderClass;
    }

    @Override
    public Collection<String> authors() {
        return List.copyOf(this.authors);
    }

    @Override
    public Collection<Dependency> dependencies() {
        return List.copyOf(this.dependencies);
    }

    private static String validatePluginName(String name) {
        if (!PLUGIN_NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException(String.format(
                    "Plugin name must follow %s pattern! %s",
                    PLUGIN_NAME_PATTERN.pattern(),
                    name
            ));
        }

        return name;
    }

    public static final class DependencyImpl implements Dependency {

        private final String name;
        private final boolean required;

        public DependencyImpl(ConfigurationNode node) throws SerializationException {
            this.name = validatePluginName(
                    Objects.requireNonNull(node.node("name").getString(), "name cannot be null")
            );
            this.required = node.node("required").getBoolean(true);
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public boolean required() {
            return this.required;
        }
    }

    public static final class DependencySerializer implements TypeSerializer<Dependency> {

        public static final DependencySerializer INSTANCE = new DependencySerializer();

        private DependencySerializer() {
        }

        @Override
        public Dependency deserialize(Type type, ConfigurationNode node) throws SerializationException {
            return new DependencyImpl(node);
        }

        @Override
        public void serialize(
                Type type,
                @Nullable Dependency dependency,
                ConfigurationNode node
        ) throws SerializationException {
            throw new SerializationException("Plugin dependencies cannot be serialized");
        }
    }
}
