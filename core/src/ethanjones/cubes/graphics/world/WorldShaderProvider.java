package ethanjones.cubes.graphics.world;

import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.client.WorldClient;
import ethanjones.cubes.world.storage.Area;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;

public class WorldShaderProvider implements ShaderProvider {
  
  private static final String defaultVertexShader = Gdx.files.internal("shaders/world.vertex.glsl").readString();
  private static final String defaultFragmentShader = Gdx.files.internal("shaders/world.fragment.glsl").readString();
  
  private CubesShader baseShader = null;
  private CubesFogShader fogShader = null;
  
  @Override
  public Shader getShader (Renderable renderable) {
    boolean fogFlag = Settings.getBooleanSettingValue(Settings.GRAPHICS_FOG);
    if (renderable.userData instanceof RenderingSettings) fogFlag &= ((RenderingSettings) renderable.userData).fogEnabled;
    if (fogFlag) {
      if (fogShader == null) {
        fogShader = new CubesFogShader(renderable);
        fogShader.init();
      }
      return fogShader;
    } else {
      if (baseShader == null) {
        baseShader = new CubesShader(renderable);
        baseShader.init();
      }
      return baseShader;
    }
  }
  
  @Override
  public void dispose () {
    if (fogShader != null) fogShader.dispose();
    if (baseShader != null) baseShader.dispose();
  }
  
  protected static class CubesShader extends DefaultShader {
    private int u_sunlight;
    private int u_lightoverride;
    private boolean lightoverride = false;
  
    protected CubesShader(Renderable renderable, String prefix) {
      super(renderable, getConfig(), prefix);
    }
  
  
    protected CubesShader(Renderable renderable) {
      this(renderable, "");
    }
    
    private static Config getConfig() {
      Config config = new Config(defaultVertexShader, defaultFragmentShader);
      config.numDirectionalLights = config.numPointLights = config.numSpotLights = config.numBones = 0;
      return config;
    }
  
    @Override
    public void init() {
      super.init();
      u_sunlight = program.fetchUniformLocation("u_sunlight", true);
      u_lightoverride = program.fetchUniformLocation("u_lightoverride", true);
    }
  
    @Override
    public void render(Renderable renderable) {
      if (renderable.userData instanceof RenderingSettings && ((RenderingSettings) renderable.userData).lightOverride != -1) {
        program.setUniformf(u_lightoverride, ((RenderingSettings) renderable.userData).lightOverride);
        lightoverride = true;
      } else if (lightoverride) {
        program.setUniformf(u_lightoverride, -1f);
        lightoverride = false;
      }
      super.render(renderable);
    }
  
    @Override
    public void begin(Camera camera, RenderContext context) {
      super.begin(camera, context);
      program.setUniformf(u_sunlight, Cubes.getClient().world.getSunlight());
      program.setUniformf(u_lightoverride, -1f);
    }
  
    @Override
    public int compareTo(Shader other) {
      return other == null ? -1 : 0;
    }
  
    @Override
    public boolean canRender(Renderable instance) {
      return true;
    }
  }
  
  protected static class CubesFogShader extends CubesShader {
    private static final String PREFIX = "#define fogflag\n";
    private int u_cameraposition;
    private int u_skycolor;
    private int u_fogdistance;
    private int u_minfogdistance;
  
    protected CubesFogShader(Renderable renderable) {
      super(renderable, PREFIX);
    }
  
    @Override
    public void init() {
      super.init();
      u_cameraposition = program.fetchUniformLocation("u_cameraposition", true);
      u_skycolor = program.fetchUniformLocation("u_skycolor", true);
      u_fogdistance = program.fetchUniformLocation("u_fogdistance", true);
      u_minfogdistance = program.fetchUniformLocation("u_minfogdistance", true);
    }
  
    @Override
    public void begin(Camera camera, RenderContext context) {
      super.begin(camera, context);
      WorldClient worldClient = ((WorldClient) Cubes.getClient().world);
      float distance = (Settings.getIntegerSettingValue(Settings.GRAPHICS_VIEW_DISTANCE) - 1) * Area.SIZE_BLOCKS;
      float fogDistance = Math.min(distance * 0.1f, 16f);
    
      program.setUniformf(u_cameraposition, Cubes.getClient().player.position);
      program.setUniformf(u_skycolor, worldClient.getSkyColour());
      program.setUniformf(u_fogdistance, fogDistance);
      program.setUniformf(u_minfogdistance, distance - fogDistance);
    }
  }
}
