package ethanjones.cubes.world.storage;

import ethanjones.cubes.core.util.Lock;
import ethanjones.cubes.core.util.Lock.HasLock;
import ethanjones.cubes.entity.Entity;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.World;
import ethanjones.data.DataGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Entities extends HashMap<UUID, Entity> implements HasLock {
  public transient final Lock lock = new Lock();
  public transient final World world;
  
  public Entities(World world) {
    this.world = world;
  }
  
  @Override
  public Lock getLock() {
    return lock;
  }
  
  public DataGroup[] getEntitiesForSave(int areaX, int areaZ) {
    lock.readLock();
    ArrayList<DataGroup> dataGroups = new ArrayList<DataGroup>();
    for (Entity entity : values()) {
      if (areaX == CoordinateConverter.area(entity.position.x) && areaZ == CoordinateConverter.area(entity.position.z) && !(entity instanceof Player)) {
        dataGroups.add(entity.write());
      }
    }
    DataGroup[] array = dataGroups.size() == 0 ? new DataGroup[0] : dataGroups.toArray(new DataGroup[dataGroups.size()]);
    lock.readUnlock();
    return array;
  }
}
