package ethanjones.cubes.common.core.mod.event;

import ethanjones.cubes.common.core.mod.ModState;

public class StoppingServerEvent extends ModEvent {

  @Override
  public ModState getModState() {
    return ModState.StoppingServer;
  }
}
