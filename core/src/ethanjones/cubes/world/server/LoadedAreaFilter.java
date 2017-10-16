package ethanjones.cubes.world.server;

import ethanjones.cubes.world.reference.AreaReference;

public interface LoadedAreaFilter {

  boolean load(AreaReference areaReference);

}
