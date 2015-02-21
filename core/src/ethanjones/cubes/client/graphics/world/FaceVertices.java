package ethanjones.cubes.client.graphics.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

public class FaceVertices {

  /*
   * vertices[vertexOffset++] = position x
   * vertices[vertexOffset++] = position y
   * vertices[vertexOffset++] = position z
   * vertices[vertexOffset++] = normal x
   * vertices[vertexOffset++] = normal y
   * vertices[vertexOffset++] = normal z
   * vertices[vertexOffset++] = texture region u
   * vertices[vertexOffset++] = texture region v
   */

  public static int createMaxY(Vector3 offset, TextureRegion region, int x, int y, int z, float[] vertices, int vertexOffset) {
    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 1;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV2();

    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 1;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV();

    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 1;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV();

    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 1;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV2();
    return vertexOffset;
  }

  public static int createMinY(Vector3 offset, TextureRegion region, int x, int y, int z, float[] vertices, int vertexOffset) {
    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 1;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV();

    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 1;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV();

    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 1;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV2();

    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 1;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV2();
    return vertexOffset;
  }

  public static int createMinX(Vector3 offset, TextureRegion region, int x, int y, int z, float[] vertices, int vertexOffset) {
    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = 1;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV2();

    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = 1;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV();

    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = 1;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV();

    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = 1;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV2();
    return vertexOffset;
  }

  public static int createMaxX(Vector3 offset, TextureRegion region, int x, int y, int z, float[] vertices, int vertexOffset) {
    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 1;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV();

    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 1;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV();

    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 1;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV2();

    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 1;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV2();
    return vertexOffset;
  }

  public static int createMinZ(Vector3 offset, TextureRegion region, int x, int y, int z, float[] vertices, int vertexOffset) {
    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 1;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV();

    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 1;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV();

    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 1;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV2();

    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 1;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV2();
    return vertexOffset;
  }

  public static int createMaxZ(Vector3 offset, TextureRegion region, int x, int y, int z, float[] vertices, int vertexOffset) {
    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 1;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV2();

    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 1;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV();

    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 1;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV();

    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = 1;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV2();
    return vertexOffset;
  }

}
