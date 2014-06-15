package ethanjones.modularworld.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.graphics.rendering.Renderer;

public class GraphicsHelper {

  public static int usage = Usage.Position | Usage.Normal | Usage.TextureCoordinates;

  public static Material load(String name) {
    Texture texture = new Texture(Gdx.files.internal(name));
    texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
    return new Material(TextureAttribute.createDiffuse(texture));
  }

  private static Renderer getRenderer() {
    return ModularWorld.instance.renderer;
  }

  public static ModelBuilder getModelBuilder() {
    return getRenderer().modelBuilder;
  }

}
