package ethanjones.cubes.core.mod.event;

import ethanjones.cubes.core.mod.ModState;

public class StoppingServerEvent extends ModEvent {

  @Override
  public ModState getModState() {
    return ModState.StoppingServer;
  }
}
