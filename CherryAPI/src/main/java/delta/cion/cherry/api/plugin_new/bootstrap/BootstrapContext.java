package xyz.endelith.plugin.bootstrap;

import xyz.endelith.event.EventManager;
import xyz.endelith.event.EventOwner;

public interface BootstrapContext extends EventOwner {

    @Override
    EventManager<BootstrapContext> eventManager();
}
