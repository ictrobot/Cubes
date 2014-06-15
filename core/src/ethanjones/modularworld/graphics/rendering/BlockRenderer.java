package ethanjones.modularworld.graphics.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.block.Block;
import ethanjones.modularworld.core.debug.Debug;
import ethanjones.modularworld.graphics.GameModel;
import ethanjones.modularworld.world.coordinates.AreaCoordinates;
import ethanjones.modularworld.world.coordinates.BlockCoordinates;
import ethanjones.modularworld.world.storage.Area;

public class BlockRenderer {
  
  public Vector3 position;
  public static int RENDERING_DISTANCE_AREAS = 2;
  
  public Environment lights;
  public PerspectiveCamera camera;
  
  private Renderer renderer;
  
  public BlockRenderer(Renderer renderer) {
    this.renderer = renderer;
    
    lights = new Environment();
    lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    
    setupCamera();
    
  }
  
  public void setupCamera() {
    camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.near = 1f;
    camera.far = 300f;
  }
  
  public void render() {
    camera.position.set(position = new Vector3(ModularWorld.instance.player.x, ModularWorld.instance.player.y, ModularWorld.instance.player.z));
    camera.update(true);

    ModularWorld.instance.player.updateRotation();
      Debug.facing();
    
    float camAngle = -getCameraCurrentXYAngle(camera) + 180;
    // camera.rotate(camAngle + 180);
    
    long l = System.currentTimeMillis();
    int r = 0;
    BlockCoordinates pos = new BlockCoordinates(position.x, position.y, position.z);
    for (int areaX = pos.areaX - RENDERING_DISTANCE_AREAS; areaX < pos.areaX + RENDERING_DISTANCE_AREAS; areaX++) {
      for (int areaY = pos.areaY - RENDERING_DISTANCE_AREAS; areaY < pos.areaY + RENDERING_DISTANCE_AREAS; areaY++) {
        for (int areaZ = pos.areaZ - RENDERING_DISTANCE_AREAS; areaZ < pos.areaZ + RENDERING_DISTANCE_AREAS; areaZ++) {
          if (areaY < 0) {
            continue;
          }
          Area area = ModularWorld.instance.world.getArea(new AreaCoordinates(areaX, areaY, areaZ));
          for (int x = 0; x < Area.S; x++) {
            for (int y = 0; y < Area.S; y++) {
              for (int z = 0; z < Area.S; z++) {
                Block b = area.getBlock(x, y, z);
                if (b != null && !Block.isCovered(x,y,z)) {
                    int aX = x + area.minBlockX;
                    int aY = y + area.minBlockY;
                    int aZ = z + area.minBlockZ;
                  GameModel i = b.getModelInstance().setPos(aX, aY, aZ);
                  if (isVisible(aX, aY, aZ)) {
                    renderer.modelBatch.render(i, lights);
                    r++;
                  }
                }
              }
            }
          }
        }
      }
    }
    long t = System.currentTimeMillis() - l;
      Debug.blockRenderer(t,r);
  }
  
  public float getCameraCurrentXYAngle(Camera cam) {
    return (float) Math.atan2(cam.up.y, cam.up.x); // * MathUtils.radiansToDegrees
  }
  
  protected boolean isVisible(int x, int y, int z) {
    return camera.frustum.sphereInFrustum(x,y,z,1);
  }
}
