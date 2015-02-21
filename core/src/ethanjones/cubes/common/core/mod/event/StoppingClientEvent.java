package ethanjones.cubes.common.core.mod.event;

import ethanjones.cubes.common.core.mod.ModState;

public class StoppingClientEvent extends ModEvent {

  @Override
  public ModState getModState() {
    return ModState.StoppingClient;
  }
}
