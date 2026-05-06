package delta.cion.msnt.event;

import delta.cion.msnt.Server;
import delta.cion.msnt.event.registration.SimpleEventRegistration;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;

import java.util.UUID;
import java.util.function.Consumer;

public abstract class DeltaEvent<T extends Event> implements AutoCloseable {
	private UUID EVENT_UUID = null;
	private Class<T> MINECRAFT_EVENT = null;

	EventNode<Event> MY_NODE;

	public DeltaEvent(Class<T> event, Consumer<T> handler) {
		 this.EVENT_UUID = UUID.randomUUID();
		 this.MINECRAFT_EVENT = event;
		 this.MY_NODE = EventNode.all(EVENT_UUID.toString());
		EventListener<Event> listener = buildEvent(new SimpleEventRegistration<>(MINECRAFT_EVENT, EVENT_UUID.toString(), handler));
		 this.MY_NODE.addListener(listener);
	}

	@SuppressWarnings("unchecked")
	private EventListener<Event> buildEvent(SimpleEventRegistration<? extends Event> registration) {
		EventListener<Event> listener;
		SimpleEventRegistration<Event> simple = (SimpleEventRegistration<Event>) registration;
		listener = EventListener.builder(simple.getEventClass())
			.handler(simple.getHandler())
			.build();
		return listener;
	}

	public void register() {
		Server.getGlobalEventHandler().addChild(this.MY_NODE);
	}

	public void unregister() {
		Server.getGlobalEventHandler().removeChild(this.MY_NODE);
	}

	public UUID getEventUuid() {
		return this.EVENT_UUID;
	}

	public Class<T> getMinecraftEvent() {
		return this.MINECRAFT_EVENT;
	}

	@Override
	public void close() {
		this.unregister();
	}
}
