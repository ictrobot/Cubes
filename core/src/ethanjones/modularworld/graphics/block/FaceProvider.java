package ethanjones.modularworld.graphics.block;

import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import ethanjones.modularworld.core.util.Direction;
import ethanjones.modularworld.world.storage.Area;

public class FaceProvider {

  public int x;
  public int y;
  public int z;

  public final int minX;
  public final int minY;
  public final int minZ;

  public int v = 0;

  public FaceProvider(Area area) {
    this.minX = area.minBlockX;
    this.minY = area.minBlockY;
    this.minZ = area.minBlockZ;
  }

  public void set(int x, int y, int z) {
    this.x = minX + x;
    this.y = minY + y;
    this.z = minZ + z;
  }

  public void addTo(MeshBuilder meshBuilder, Direction side) {
    switch (side) {
      case posX:
        meshBuilder.rect(x + 1, y, z, x, y, z, x, y + 1, z, x + 1, y + 1, 0, x + 1, y, z);
        v++;
        break;
      case negX:
        meshBuilder.rect(x, y, z + 1, x + 1, y, z + 1, x + 1, y + 1, z + 1, x, y + 1, 1, x + 1, y, z);
        v++;
        break;
      case posY:
        meshBuilder.rect(x + 1, y + 1, z + 1, x + 1, y + 1, z, x, y + 1, z, x, y + 1, 1, x, y + 1, z);
        v++;
        break;
      case negY:
        meshBuilder.rect(x, y, z, x + 1, y, z, x + 1, y, z + 1, x, y, 1, x, y + 1, z);
        v++;
        break;
      case posZ:
        meshBuilder.rect(x, y, z, x, y, z + 1, x, y + 1, z + 1, x, y + 1, 0, x, y, z + 1);
        v++;
        break;
      case negZ:
        meshBuilder.rect(x + 1, y, z + 1, x + 1, y, z, x + 1, y + 1, z, x + 1, y + 1, 1, x, y, z + 1);
        v++;
        break;
    }
    /**
     switch (side) {
     case posX:
     meshBuilder.rect(x + 1, y + 0, z + 0, x + 0, y + 0, z + 0, x + 0, y + 1, z + 0, x + 1, y + 1, 0, x + 1, y + 0, z + 0);
     break;
     case negX:
     meshBuilder.rect(x + 0, y + 0, z + 1, x + 1, y + 0, z + 1, x + 1, y + 1, z + 1, x + 0, y + 1, 1, x + 1, y + 0, z + 0);
     break;
     case posY:
     meshBuilder.rect(x + 1, y + 1, z + 1, x + 1, y + 1, z + 0, x + 0, y + 1, z + 0, x + 0, y + 1, 1, x + 0, y + 1, z + 0);
     break;
     case negY:
     meshBuilder.rect(x + 0, y + 0, z + 0, x + 1, y + 0, z + 0, x + 1, y + 0, z + 1, x + 0, y + 0, 1, x + 0, y + 1, z + 0);
     break;
     case posZ:
     meshBuilder.rect(x + 0, y + 0, z + 0, x + 0, y + 0, z + 1, x + 0, y + 1, z + 1, x + 0, y + 1, 0, x + 0, y + 0, z + 1);
     break;
     case negZ:
     meshBuilder.rect(x + 1, y + 0, z + 1, x + 1, y + 0, z + 0, x + 1, y + 1, z + 0, x + 1, y + 1, 1, x + 0, y + 0, z + 1);
     break;
     }
     **/
  }
}
