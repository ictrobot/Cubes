package ethanjones.cubes.core.mod.event;

import ethanjones.cubes.core.mod.ModState;

public class StartingClientEvent extends ModEvent {

  @Override
  public ModState getModState() {
    return ModState.StartingClient;
  }
}
