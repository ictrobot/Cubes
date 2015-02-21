package ethanjones.cubes.common.core.mod.event;

import ethanjones.cubes.common.core.mod.ModState;

public class PostInitializationEvent extends ModEvent {

  @Override
  public ModState getModState() {
    return ModState.PostInitialization;
  }
}
