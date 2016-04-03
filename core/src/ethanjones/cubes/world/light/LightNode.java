package ethanjones.cubes.world.light;

import ethanjones.cubes.world.storage.Area;

public class LightNode {
  public Area area;
  public int x;
  public int y;
  public int z;
  public int l;

  public LightNode() {
  }

  ;

  public LightNode(Area area, int x, int y, int z, int l) {
    this.area = area;
    this.x = x;
    this.y = y;
    this.z = z;
    this.l = l;
  }
}
