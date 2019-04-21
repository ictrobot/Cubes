package ethanjones.cubes.world.storage;

import ethanjones.cubes.core.util.locks.Locked;
import ethanjones.cubes.core.gwt.UUID;
import ethanjones.cubes.entity.Entity;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.thread.WorldLockable;
import ethanjones.data.DataGroup;

import java.util.ArrayList;
import java.util.HashMap;

public class Entities extends WorldLockable {
  public final World world;
  public final HashMap<UUID, Entity> map;
  
  public Entities(World world) {
    super(Type.ENTITIES, world.side);
    this.world = world;
    this.map = new HashMap<>();
  }
  
  public DataGroup[] getEntitiesForSave(int areaX, int areaZ) {
    try (Locked<WorldLockable> locked = acquireReadLock()) {
      ArrayList<DataGroup> dataGroups = new ArrayList<DataGroup>();
      for (Entity entity : map.values()) {
        if (areaX == CoordinateConverter.area(entity.position.x) && areaZ == CoordinateConverter.area(entity.position.z) && !(entity instanceof Player)) {
          dataGroups.add(entity.write());
        }
      }
      return dataGroups.size() == 0 ? new DataGroup[0] : dataGroups.toArray(new DataGroup[dataGroups.size()]);
    }
  }
}
