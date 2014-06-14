package ethanjones.modularworld.graphics.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.block.Block;
import ethanjones.modularworld.graphics.GameObject;
import ethanjones.modularworld.world.coordinates.AreaCoordinates;
import ethanjones.modularworld.world.coordinates.BlockCoordinates;
import ethanjones.modularworld.world.storage.Area;

public class Renderer {
  
  public Vector3 position;
  public static int RENDERING_DISTANCE_AREAS = 2;
  
  public Environment lights;
  public PerspectiveCamera camera;
  public ModelBatch modelBatch;
  public ModelBuilder modelBuilder;
  
  public Renderer() {
    
    position = new Vector3(40, 10, 40);
    
    lights = new Environment();
    lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    
    setupCamera();
    
    modelBatch = new ModelBatch();
    modelBuilder = new ModelBuilder();
  }
  
  public void setupCamera() {
    camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.position.set(position);
    camera.lookAt(0, 0, 0);
    camera.near = 1f;
    camera.far = 300f;
    camera.update();
  }
  
  public void render() {
    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    
    modelBatch.begin(camera);
    long l = System.currentTimeMillis();
    int r = 0;
    BlockCoordinates pos = new BlockCoordinates(position.x, position.y, position.z);
    for (int areaX = pos.areaX - RENDERING_DISTANCE_AREAS; areaX < pos.areaX + RENDERING_DISTANCE_AREAS; areaX++) {
      for (int areaY = pos.areaY - RENDERING_DISTANCE_AREAS; areaY < pos.areaY + RENDERING_DISTANCE_AREAS; areaY++) {
        for (int areaZ = pos.areaZ - RENDERING_DISTANCE_AREAS; areaZ < pos.areaZ + RENDERING_DISTANCE_AREAS; areaZ++) {
          if (areaY < 0) {
            continue;
          }
          AreaCoordinates areaC = new AreaCoordinates(areaX, areaY, areaZ);
          Area area = ModularWorld.instance.world.getArea(areaC);
          for (int x = 0; x < Area.S; x++) {
            for (int y = 0; y < Area.S; y++) {
              for (int z = 0; z < Area.S; z++) {
                Block b = area.getBlock(x, y, z);
                if (b != null) { // && !b.isCovered(x, y, z)
                  GameObject i = b.getModelInstance(x, y, z);
                  if (isVisible(camera, i)) {
                    modelBatch.render(i, lights);
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
    System.out.println(t + " " + (1000 / t) + " " + r);
    modelBatch.end();
  }
  
  protected boolean isVisible(final Camera cam, final GameObject instance) {
    return true;// cam.frustum.boundsInFrustum(instance.bounds);
  }
  
  public void dispose() {
    modelBatch.dispose();
  }
}
