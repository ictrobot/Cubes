package ethanjones.cubes.core.mod.event;

import ethanjones.cubes.core.mod.ModState;

public class InitializationEvent extends ModEvent {
  
  @Override
  public ModState getModState() {
    return ModState.Initialization;
  }
}
