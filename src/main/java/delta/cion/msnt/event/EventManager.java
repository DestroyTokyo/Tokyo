package delta.cion.msnt.event;

import delta.cion.msnt.event.registration.BuilderEventRegistration;
import delta.cion.msnt.event.registration.EventRegistration;
import delta.cion.msnt.event.registration.SimpleEventRegistration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import java.util.function.Consumer;

public final class EventManager {

	private static final EventNode<Event> globalEventHandler = MinecraftServer.getGlobalEventHandler();

	private EventManager() {}

	public static <T extends Event> void onEvent(Class<T> eventClass, Consumer<T> handler) {
		onEvent(eventClass, null, handler);
	}

	public static <T extends Event> void onEvent(Class<T> eventClass, String nodeName, Consumer<T> handler) {
		registerListener(new SimpleEventRegistration<>(eventClass, nodeName, handler));
	}

	public static <T extends Event> void onBuilderEvent(Class<T> eventClass, Consumer<EventListener.Builder<T>> builder) {
		onBuilderEvent(eventClass, null, builder);
	}

	public static <T extends Event> void onBuilderEvent(Class<T> eventClass, String nodeName, Consumer<EventListener.Builder<T>> builder) {
		registerListener(new BuilderEventRegistration<>(eventClass, nodeName, builder));
	}

	public static void registerHandler(EventHandler eventHandler) {
		for (EventRegistration<? extends Event> registration : eventHandler.eventRegistrations) {
			registerListener(registration);
		}
	}

	@SuppressWarnings("unchecked")
	private static void registerListener(EventRegistration<? extends Event> registration) {
		EventListener<Event> listener;
		if (registration instanceof SimpleEventRegistration) {
			SimpleEventRegistration<Event> simple = (SimpleEventRegistration<Event>) registration;
			listener = EventListener.builder(simple.getEventClass())
				.handler(simple.getHandler())
				.build();
		} else if (registration instanceof BuilderEventRegistration) {
			BuilderEventRegistration<Event> builderReg = (BuilderEventRegistration<Event>) registration;
			EventListener.Builder<Event> builder = EventListener.builder(builderReg.getEventClass());
			builderReg.getBuilder().accept(builder);
			listener = builder.build();
		} else {
			throw new IllegalArgumentException("Unknown registration type: " + registration.getClass());
		}

		String nodeName = registration.getEventNode();
		if (nodeName == null) {
			globalEventHandler.addListener(listener);
		} else {
			EventNode.all(nodeName).addListener(listener);
		}
	}
}
