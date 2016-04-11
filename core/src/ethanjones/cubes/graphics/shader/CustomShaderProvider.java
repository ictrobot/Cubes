package ethanjones.cubes.graphics.shader;

import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.light.SunLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;

public class CustomShaderProvider extends DefaultShaderProvider {

  private static int u_sunlight;

  public CustomShaderProvider() {
    super(Gdx.files.internal("shaders/world.vertex.glsl"), Gdx.files.internal("shaders/world.fragment.glsl"));
  }

  @Override
  protected Shader createShader(final Renderable renderable) {
    return new DefaultShader(renderable, config) {
      @Override
      public void init() {
        super.init();
        u_sunlight = program.fetchUniformLocation("u_sunlight", true);
      }

      @Override
      public void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);
        program.setUniformf(u_sunlight, Cubes.getClient().world.getSunlight());
      }
    };
  }
}
