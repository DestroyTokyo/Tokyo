package delta.cion.msnt.event.registration;

import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import java.util.function.Consumer;

public final class BuilderEventRegistration<T extends Event> extends EventRegistration<T> {

	private final Consumer<EventListener.Builder<T>> builder;

	public BuilderEventRegistration(Class<T> eventClass, String eventNode, Consumer<EventListener.Builder<T>> builder) {
		super(eventClass, eventNode);
		this.builder = builder;
	}

	public Consumer<EventListener.Builder<T>> getBuilder() {
		return builder;
	}
}
