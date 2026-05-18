package delta.cion.cherry.server.event.registration;

import net.minestom.server.event.Event;
import java.util.function.Consumer;

public record EventRegistration<T extends Event>(Class<T> eventClass, String eventNode, Consumer<T> handler) { }
