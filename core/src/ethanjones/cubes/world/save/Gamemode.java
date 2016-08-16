package ethanjones.cubes.world.save;

import ethanjones.cubes.core.localization.Localization;

public enum Gamemode {
  survival, creative;

  @Override
  public String toString() {
    return Localization.get("gamemode." + name());
  }

}
