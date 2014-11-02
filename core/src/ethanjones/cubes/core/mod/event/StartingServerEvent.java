package ethanjones.cubes.core.mod.event;

import ethanjones.cubes.core.mod.ModState;

public class StartingServerEvent extends ModEvent {

  @Override
  public ModState getModState() {
    return ModState.StartingServer;
  }
}
