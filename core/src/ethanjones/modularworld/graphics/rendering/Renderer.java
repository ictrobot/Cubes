package ethanjones.modularworld.graphics.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.block.Block;
import ethanjones.modularworld.world.World;

public class Renderer {
  int posX = 10;
  int posY = 10;
  int posZ = 10;
  public Environment lights;
  public PerspectiveCamera camera;
  public ModelBatch modelBatch;
  public ModelBuilder modelBuilder;
  
  public Renderer() {
    lights = new Environment();
    lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    
    setupCamera();
    
    modelBatch = new ModelBatch();
    modelBuilder = new ModelBuilder();
  }
  
  public void setupCamera() {
    camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.position.set(posX, posY, posZ);
    camera.lookAt(0, 0, 0);
    camera.near = 1f;
    camera.far = 300f;
    camera.update();
  }
  
  public void render() {
    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    
    modelBatch.begin(camera);
    Block b;
    long l = System.currentTimeMillis();
    int r = 0;
    for (int x = posX - 64; x < posX + 64; x++) {
      for (int z = posZ - 64; z < posZ + 64; z++) {
        for (int y = 0; y < World.HEIGHT_LIMIT; y++) {
          b = ModularWorld.instance.world.getBlock(x, y, z);
          if (b != null) { // && !b.isCovered(x, y, z)
            modelBatch.render(b.getModelInstance(x, y, z), lights);
            r++;
          }
        }
      }
    }
    System.out.println((System.currentTimeMillis() - l) + " " + r);
    modelBatch.end();
  }
  
  public void dispose() {
    modelBatch.dispose();
  }
}
