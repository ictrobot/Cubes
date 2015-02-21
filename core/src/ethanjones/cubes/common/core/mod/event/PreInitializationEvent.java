package ethanjones.cubes.common.core.mod.event;

import ethanjones.cubes.common.core.mod.ModState;

public class PreInitializationEvent extends ModEvent {

  @Override
  public ModState getModState() {
    return ModState.PreInitialization;
  }
}
