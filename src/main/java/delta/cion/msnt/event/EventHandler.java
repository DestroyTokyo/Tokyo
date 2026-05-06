package delta.cion.msnt.event;

import delta.cion.msnt.event.registration.BuilderEventRegistration;
import delta.cion.msnt.event.registration.EventRegistration;
import delta.cion.msnt.event.registration.SimpleEventRegistration;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class EventHandler {

	final List<EventRegistration<? extends Event>> eventRegistrations = new ArrayList<>();
	final String nodeName;

	public EventHandler(Consumer<EventHandler> eventSetup) {
		this(eventSetup, null);
	}

	public EventHandler(Consumer<EventHandler> eventSetup, String nodeName) {
		this.nodeName = nodeName;
		eventSetup.accept(this);
	}

	public <T extends Event> void onEvent(Class<T> eventClass, Consumer<T> handler) {
		eventRegistrations.add(new SimpleEventRegistration<>(eventClass, nodeName, handler));
	}

	public <T extends Event> void onBuilderEvent(Class<T> eventClass, Consumer<EventListener.Builder<T>> builder) {
		eventRegistrations.add(new BuilderEventRegistration<>(eventClass, nodeName, builder));
	}
}
