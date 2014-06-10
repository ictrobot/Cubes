package ethanjones.modularworld.graphics.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class BasicRenderer {
  public Environment lights;
  public PerspectiveCamera camera;
  public ModelBatch modelBatch;

  public Model model;
  public ModelInstance instance;
  
  public BasicRenderer() {
    lights = new Environment();
    lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    
    modelBatch = new ModelBatch();
    
    ModelBuilder modelBuilder = new ModelBuilder();
    model = modelBuilder.createBox(5f, 5f, 5f, new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position | Usage.Normal);
    instance = new ModelInstance(model);
  }
  
  public void setupCamera() {
    camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.position.set(10f, 10f, 10f);
    camera.lookAt(0, 0, 0);
    camera.near = 1f;
    camera.far = 300f;
    camera.update();
  }
  

  public void render() {
    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    
    modelBatch.begin(camera);
    modelBatch.render(instance, lights);
    modelBatch.end();
  }
  
  public void dispose() {
    modelBatch.dispose();
    model.dispose();
  }
}
