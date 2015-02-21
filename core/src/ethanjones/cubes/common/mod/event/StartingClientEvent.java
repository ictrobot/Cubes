package ethanjones.cubes.common.mod.event;

import ethanjones.cubes.common.mod.ModState;

public class StartingClientEvent extends ModEvent {

  @Override
  public ModState getModState() {
    return ModState.StartingClient;
  }
}
