package ethanjones.cubes.world.reference.multi;

import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.reference.AreaReference;

import com.badlogic.gdx.math.Vector3;

import java.util.HashSet;
import java.util.Set;

public class WorldRegion implements MultiAreaReference {
  public final int minAreaX;
  public final int maxAreaX;
  public final int minAreaZ;
  public final int maxAreaZ;
  
  public WorldRegion(AreaReference areaReference) {
    this(areaReference.areaX, areaReference.areaX, areaReference.areaZ, areaReference.areaZ);
  }
  
  public WorldRegion(AreaReference areaReference, int range) {
    this(areaReference.areaX - range, areaReference.areaX + range, areaReference.areaZ - range, areaReference.areaZ + range);
  }
  
  public WorldRegion(int minAreaX, int maxAreaX, int minAreaZ, int maxAreaZ) {
    this.minAreaX = minAreaX;
    this.maxAreaX = maxAreaX;
    this.minAreaZ = minAreaZ;
    this.maxAreaZ = maxAreaZ;
  }
  
  public boolean contains(AreaReference a) {
    return a.areaX >= minAreaX && a.areaX <= maxAreaX && a.areaZ >= minAreaZ && a.areaZ <= maxAreaZ;
  }
  
  public boolean contains(Vector3 position) {
    int areaX = CoordinateConverter.area(position.x);
    int areaZ = CoordinateConverter.area(position.z);
    return areaX >= minAreaX && areaX <= maxAreaX && areaZ >= minAreaZ && areaZ <= maxAreaZ;
  }
  
  public AreaReference getCenter() {
    AreaReference areaReference = new AreaReference();
    areaReference.areaX = minAreaX + ((maxAreaX - minAreaX + 1) / 2);
    areaReference.areaZ = minAreaZ + ((maxAreaZ - minAreaZ + 1) / 2);
    areaReference.modified();
    return areaReference;
  }
  
  @Override
  public Set<AreaReference> getAreaReferences() {
    HashSet<AreaReference> set = new HashSet<AreaReference>((maxAreaX - minAreaX) * (maxAreaZ - minAreaZ));
    
    AreaReference base = new AreaReference().setFromAreaCoordinates(minAreaX, minAreaZ);
    for (int x = 0; x <= (maxAreaX - minAreaX); x++) {
      for (int z = 0; z <= (maxAreaZ - minAreaZ); z++) {
        set.add(base.clone().offset(x, z));
      }
    }
    return set;
  }
}
