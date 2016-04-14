package ethanjones.cubes.graphics.world;

import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.light.LightNode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;

public class WorldShaderProvider extends DefaultShaderProvider {

  private static int u_sunlight;
  private static int u_lightoverride;
  private static boolean lightoverride = false;

  public WorldShaderProvider() {
    super(Gdx.files.internal("shaders/world.vertex.glsl"), Gdx.files.internal("shaders/world.fragment.glsl"));
  }

  @Override
  protected Shader createShader(final Renderable renderable) {
    return new DefaultShader(renderable, config) {
      @Override
      public void init() {
        super.init();
        u_sunlight = program.fetchUniformLocation("u_sunlight", true);
        u_lightoverride = program.fetchUniformLocation("u_lightoverride", true);
      }

      @Override
      public void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);
        program.setUniformf(u_sunlight, Cubes.getClient().world.getSunlight());
        program.setUniformf(u_lightoverride, -1f);
      }

      @Override
      public void render(Renderable renderable) {
        if (renderable.userData instanceof LightNode) {
          program.setUniformf(u_lightoverride, ((LightNode) renderable.userData).l);
          lightoverride = true;
        } else if (lightoverride) {
          program.setUniformf(u_lightoverride, -1f);
          lightoverride = false;
        }
        super.render(renderable);
      }
    };
  }
}
