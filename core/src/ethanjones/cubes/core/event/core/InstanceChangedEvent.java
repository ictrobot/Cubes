package ethanjones.cubes.core.event.core;

import ethanjones.cubes.core.event.Event;

public class InstanceChangedEvent extends Event {

  public InstanceChangedEvent() {
    super(false, false);
  }

  public static class ClientChangedEvent extends InstanceChangedEvent {

  }

  public static class ServerChangedEvent extends InstanceChangedEvent {

  }

  public static class MenuChangedEvent extends InstanceChangedEvent {

  }
}
