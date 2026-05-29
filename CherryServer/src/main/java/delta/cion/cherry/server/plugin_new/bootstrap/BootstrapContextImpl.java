package delta.cion.cherry.server.plugin_new.bootstrap;

import java.util.Objects;
import xyz.endelith.plugin.bootstrap.BootstrapContext;
import xyz.endelith.server.event.EventManagerImpl;

public final class BootstrapContextImpl implements BootstrapContext {

    private final EventManagerImpl<BootstrapContext> eventManager;

    public BootstrapContextImpl(EventManagerImpl<BootstrapContext> eventManager) {
        this.eventManager = Objects.requireNonNull(eventManager, "event manager");
    }

    @Override
    public EventManagerImpl<BootstrapContext> eventManager() {
        return this.eventManager;
    }
}
