package ethanjones.cubes.common.core.mod.event;

import ethanjones.cubes.common.core.mod.ModState;

public class StartingClientEvent extends ModEvent {

  @Override
  public ModState getModState() {
    return ModState.StartingClient;
  }
}
