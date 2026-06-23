package delta.cion.tokyo.api.event;

import delta.cion.tokyo.api.plugin.Plugin;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;

import java.util.UUID;
import java.util.function.Consumer;

public class DeltaEvent<T extends Event> implements AutoCloseable {
	private final UUID EVENT_UUID;
	private final Class<T> MINECRAFT_EVENT;

	private final EventNode<Event> EVENT_NODE;
	private final EventListener<Event> EVENT_LISTENER;

	public DeltaEvent(Class<T> event, Consumer<T> handler) {
		this.EVENT_UUID = UUID.randomUUID();
		this.MINECRAFT_EVENT = event;
		this.EVENT_NODE = EventNode.all(EVENT_UUID.toString());
		this.EVENT_LISTENER = buildEvent(new EventRegistration<>(MINECRAFT_EVENT, EVENT_UUID.toString(), handler));
		this.EVENT_NODE.addListener(EVENT_LISTENER);
	}

	@SuppressWarnings("unchecked")
	private EventListener<Event> buildEvent(EventRegistration<? extends Event> registration) {
		EventListener<Event> listener;
		EventRegistration<Event> simple = (EventRegistration<Event>) registration;
		listener = EventListener.builder(simple.eventClass())
			.handler(simple.handler())
			.build();
		return listener;
	}

	public void register() {
		Plugin.getGlobalEventHandler().addChild(this.EVENT_NODE);
	}

	public void unregister() {
		Plugin.getGlobalEventHandler().removeChild(this.EVENT_NODE);
	}

	public UUID getEventUuid() {
		return this.EVENT_UUID;
	}

	public Class<T> getMinecraftEvent() {
		return this.MINECRAFT_EVENT;
	}

	public EventListener<Event> getEventListener() {
		return this.EVENT_LISTENER;
	}

	@Override
	public void close() {
		this.unregister();
	}
}
