package ethanjones.cubes.graphics.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

public class FaceVertices {

  private static final int maxY = 0 << 8;
  private static final int minY = 0 << 8;
  private static final int maxX = 1 << 8;
  private static final int minX = 1 << 8;
  private static final int maxZ = 2 << 8;
  private static final int minZ = 2 << 8;

  /*
   * vertices[vertexOffset++] = position x
   * vertices[vertexOffset++] = position y
   * vertices[vertexOffset++] = position z
   * vertices[vertexOffset++] = texture region u
   * vertices[vertexOffset++] = texture region v
   * vertices[vertexOffset++] = light
   * vertices[vertexOffset++] = ao u
   * vertices[vertexOffset++] = au v
   */

  public static int createMaxY(Vector3 offset, TextureRegion region, TextureRegion ao, int x, int y, int z, int light, float[] vertices, int vertexOffset) {
    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light + maxY;
    if (ao != null) {
      vertices[vertexOffset++] = ao.getU2();
      vertices[vertexOffset++] = ao.getV2();
    }

    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light + maxY;
    if (ao != null) {
      vertices[vertexOffset++] = ao.getU2();
      vertices[vertexOffset++] = ao.getV();
    }

    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light + maxY;
    if (ao != null) {
      vertices[vertexOffset++] = ao.getU();
      vertices[vertexOffset++] = ao.getV();
    }

    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light + maxY;
    if (ao != null) {
      vertices[vertexOffset++] = ao.getU();
      vertices[vertexOffset++] = ao.getV2();
    }

    return vertexOffset;
  }

  public static int createMinY(Vector3 offset, TextureRegion region, TextureRegion ao, int x, int y, int z, int light, float[] vertices, int vertexOffset) {
    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light + minY;
    if (ao != null) {
      vertices[vertexOffset++] = ao.getU();
      vertices[vertexOffset++] = ao.getV();
    }

    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light + minY;
    if (ao != null) {
      vertices[vertexOffset++] = ao.getU2();
      vertices[vertexOffset++] = ao.getV();
    }

    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light + minY;
    if (ao != null) {
      vertices[vertexOffset++] = ao.getU2();
      vertices[vertexOffset++] = ao.getV2();
    }

    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light + minY;
    if (ao != null) {
      vertices[vertexOffset++] = ao.getU();
      vertices[vertexOffset++] = ao.getV2();
    }

    return vertexOffset;
  }

  public static int createMaxX(Vector3 offset, TextureRegion region, TextureRegion ao, int x, int y, int z, int light, float[] vertices, int vertexOffset) {
    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light + maxX;
    if (ao != null) {
      vertices[vertexOffset++] = ao.getU2();
      vertices[vertexOffset++] = ao.getV();
    }

    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light + maxX;
    if (ao != null) {
      vertices[vertexOffset++] = ao.getU();
      vertices[vertexOffset++] = ao.getV();
    }

    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light + maxX;
    if (ao != null) {
      vertices[vertexOffset++] = ao.getU();
      vertices[vertexOffset++] = ao.getV2();
    }

    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light + maxX;
    if (ao != null) {
      vertices[vertexOffset++] = ao.getU2();
      vertices[vertexOffset++] = ao.getV2();
    }

    return vertexOffset;
  }

  public static int createMinX(Vector3 offset, TextureRegion region, TextureRegion ao, int x, int y, int z, int light, float[] vertices, int vertexOffset) {
    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light + minX;
    if (ao != null) {
      vertices[vertexOffset++] = ao.getU();
      vertices[vertexOffset++] = ao.getV2();
    }

    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light + minX;
    if (ao != null) {
      vertices[vertexOffset++] = ao.getU();
      vertices[vertexOffset++] = ao.getV();
    }

    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light + minX;
    if (ao != null) {
      vertices[vertexOffset++] = ao.getU2();
      vertices[vertexOffset++] = ao.getV();
    }

    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light + minX;
    if (ao != null) {
      vertices[vertexOffset++] = ao.getU2();
      vertices[vertexOffset++] = ao.getV2();
    }

    return vertexOffset;
  }

  public static int createMaxZ(Vector3 offset, TextureRegion region, TextureRegion ao, int x, int y, int z, int light, float[] vertices, int vertexOffset) {
    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light + maxZ;
    if (ao != null) {
      vertices[vertexOffset++] = ao.getU();
      vertices[vertexOffset++] = ao.getV2();
    }

    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light + maxZ;
    if (ao != null) {
      vertices[vertexOffset++] = ao.getU();
      vertices[vertexOffset++] = ao.getV();
    }

    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light + maxZ;
    if (ao != null) {
      vertices[vertexOffset++] = ao.getU2();
      vertices[vertexOffset++] = ao.getV();
    }

    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light + maxZ;
    if (ao != null) {
      vertices[vertexOffset++] = ao.getU2();
      vertices[vertexOffset++] = ao.getV2();
    }

    return vertexOffset;
  }

  public static int createMinZ(Vector3 offset, TextureRegion region, TextureRegion ao, int x, int y, int z, int light, float[] vertices, int vertexOffset) {
    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light + minZ;
    if (ao != null) {
      vertices[vertexOffset++] = ao.getU2();
      vertices[vertexOffset++] = ao.getV();
    }

    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light + minZ;
    if (ao != null) {
      vertices[vertexOffset++] = ao.getU();
      vertices[vertexOffset++] = ao.getV();
    }

    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light + minZ;
    if (ao != null) {
      vertices[vertexOffset++] = ao.getU();
      vertices[vertexOffset++] = ao.getV2();
    }

    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light + minZ;
    if (ao != null) {
      vertices[vertexOffset++] = ao.getU2();
      vertices[vertexOffset++] = ao.getV2();
    }

    return vertexOffset;
  }

}
