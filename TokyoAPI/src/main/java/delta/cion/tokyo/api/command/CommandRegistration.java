package delta.cion.tokyo.api.command;

import net.minestom.server.command.builder.Command;

import java.util.UUID;
import java.util.function.Consumer;

public record CommandRegistration<T extends Command>(Class<T> commandClass, UUID commandUUID, Consumer<T> handler) {}
