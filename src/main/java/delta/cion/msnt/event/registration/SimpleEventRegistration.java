package delta.cion.msnt.event.registration;

import net.minestom.server.event.Event;

import java.util.function.Consumer;

public class SimpleEventRegistration<T extends Event> extends EventRegistration<T> {

	private final Consumer<T> handler;

	public SimpleEventRegistration(Class<T> eventClass, String eventNode, Consumer<T> handler) {
		super(eventClass, eventNode);
		this.handler = handler;
	}

	public Consumer<T> getHandler() {
		return handler;
	}
}
