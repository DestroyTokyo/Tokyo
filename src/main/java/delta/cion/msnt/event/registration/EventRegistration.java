package delta.cion.msnt.event.registration;

import net.minestom.server.event.Event;

public abstract class EventRegistration<T extends Event> {

	private final Class<T> eventClass;
	private final String eventNode;

	protected EventRegistration(Class<T> eventClass, String eventNode) {
		this.eventClass = eventClass;
		this.eventNode = eventNode;
	}

	public Class<T> getEventClass() {
		return eventClass;
	}

	public String getEventNode() {
		return eventNode;
	}
}
