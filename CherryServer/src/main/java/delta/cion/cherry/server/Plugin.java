package delta.cion.cherry.server.plugin;

import java.util.UUID;

public abstract class Plugin {

	private final String name;
	private final UUID id;
	private Status status;

	public Plugin(String name) {
		this.name = name;
		this.id = UUID.randomUUID();
		this.status = Status.DISABLED;
	}

	public String getName() {
		return name;
	}

	public UUID getId() {
		return id;
	}

	public Status getStatus() {
		return status;
	}

	public void onLoad() {}

	public abstract void onEnable();

	public abstract void onDisable();

	public void updateStatus(Status newStatus) {
		this.status = newStatus;
	}

	public enum Status {
		ENABLED, DISABLED, OUTDATED
	}
}
