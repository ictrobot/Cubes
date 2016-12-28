package ethanjones.cubes.graphics.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

public class CrossFaceVertices {
  private static final float min = (float) ((1 - (Math.sqrt(2) / 2)) / 2);
  private static final float max = 1 - min;
  
  public static int createMinXMaxZStretched(Vector3 offset, TextureRegion region, int x, int y, int z, int light, float[] vertices, int vertexOffset) {
    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light;
    
    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light;
    
    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light;
    
    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light;
    return vertexOffset;
  }
  
  public static int createMaxZMinXStretched(Vector3 offset, TextureRegion region, int x, int y, int z, int light, float[] vertices, int vertexOffset) {
    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light;
    
    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light ;
    
    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light ;
    
    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light ;
    return vertexOffset;
  }
  
  public static int createMaxXMinZStretched(Vector3 offset, TextureRegion region, int x, int y, int z, int light, float[] vertices, int vertexOffset) {
    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light;
  
    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light;
  
    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light;
  
    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light;
    return vertexOffset;
  }
  
  
  public static int createMinZMaxXStretched(Vector3 offset, TextureRegion region, int x, int y, int z, int light, float[] vertices, int vertexOffset) {
    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light;
  
    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light ;
  
    vertices[vertexOffset++] = offset.x + x;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z + 1;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light ;
  
    vertices[vertexOffset++] = offset.x + x + 1;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light ;
    return vertexOffset;
  }
  
  public static int createMinXMaxZ(Vector3 offset, TextureRegion region, int x, int y, int z, int light, float[] vertices, int vertexOffset) {
    vertices[vertexOffset++] = offset.x + x + min;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z + min;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light;
    
    vertices[vertexOffset++] = offset.x + x + max;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z + max;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light;
    
    vertices[vertexOffset++] = offset.x + x + max;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z + max;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light;
    
    vertices[vertexOffset++] = offset.x + x + min;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z + min;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light;
    return vertexOffset;
  }
  
  public static int createMaxZMinX(Vector3 offset, TextureRegion region, int x, int y, int z, int light, float[] vertices, int vertexOffset) {
    vertices[vertexOffset++] = offset.x + x + min;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z + min;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light;
    
    vertices[vertexOffset++] = offset.x + x + max;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z + max;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light ;
    
    vertices[vertexOffset++] = offset.x + x + max;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z + max;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light ;
    
    vertices[vertexOffset++] = offset.x + x + min;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z + min;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light ;
    return vertexOffset;
  }
  
  public static int createMaxXMinZ(Vector3 offset, TextureRegion region, int x, int y, int z, int light, float[] vertices, int vertexOffset) {
    vertices[vertexOffset++] = offset.x + x + max;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z + min;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light;
    
    vertices[vertexOffset++] = offset.x + x + min;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z + max;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light;
    
    vertices[vertexOffset++] = offset.x + x + min;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z + max;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light;
    
    vertices[vertexOffset++] = offset.x + x + max;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z + min;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light;
    return vertexOffset;
  }
  
  
  public static int createMinZMaxX(Vector3 offset, TextureRegion region, int x, int y, int z, int light, float[] vertices, int vertexOffset) {
    vertices[vertexOffset++] = offset.x + x + max;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z + min;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light;
    
    vertices[vertexOffset++] = offset.x + x + min;
    vertices[vertexOffset++] = offset.y + y;
    vertices[vertexOffset++] = offset.z + z + max;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV2();
    vertices[vertexOffset++] = light ;
    
    vertices[vertexOffset++] = offset.x + x + min;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z + max;
    vertices[vertexOffset++] = region.getU2();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light ;
    
    vertices[vertexOffset++] = offset.x + x + max;
    vertices[vertexOffset++] = offset.y + y + 1;
    vertices[vertexOffset++] = offset.z + z + min;
    vertices[vertexOffset++] = region.getU();
    vertices[vertexOffset++] = region.getV();
    vertices[vertexOffset++] = light ;
    return vertexOffset;
  }
}
