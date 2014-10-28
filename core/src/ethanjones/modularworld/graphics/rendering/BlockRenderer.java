package ethanjones.modularworld.graphics.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.utils.Disposable;
import ethanjones.modularworld.core.settings.Settings;
import ethanjones.modularworld.graphics.world.AreaRenderer;
import ethanjones.modularworld.graphics.world.AreaRendererPool;
import ethanjones.modularworld.input.CameraController;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.world.WorldClient;
import ethanjones.modularworld.world.reference.AreaReference;
import ethanjones.modularworld.world.storage.Area;

public class BlockRenderer implements Disposable {

  public Environment environment;
  public PerspectiveCamera camera;

  private ModelBatch modelBatch;

  private AreaRendererPool areaRendererPool = new AreaRendererPool();

  public BlockRenderer() {
    modelBatch = new ModelBatch();

    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

    camera = new PerspectiveCamera(Settings.getIntegerSettingValue(Settings.GRAPHICS_FOV), Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) {
      @Override
      public void update(boolean b) {
        viewportWidth = Gdx.graphics.getWidth();
        viewportHeight = Gdx.graphics.getHeight();
        super.update(b);
      }
    };
    camera.near = 0.1f;
    camera.far = 300f;

    ModularWorldClient.instance.inputChain.cameraController = new CameraController(camera);
  }

  public void render() {
    modelBatch.begin(camera);

    int renderDistance = Settings.getIntegerSettingValue(Settings.GRAPHICS_VIEW_DISTANCE);

    AreaReference pos = ((WorldClient) ModularWorldClient.instance.world).playerArea;
    for (int areaX = pos.areaX - renderDistance; areaX <= pos.areaX + renderDistance; areaX++) {
      for (int areaY = Math.max(pos.areaY - renderDistance, 0); areaY <= pos.areaY + renderDistance; areaY++) {
        for (int areaZ = pos.areaZ - renderDistance; areaZ <= pos.areaZ + renderDistance; areaZ++) {
          Area area = ModularWorldClient.instance.world.getArea(areaX, areaY, areaZ);
          if (areaInFrustum(area, camera.frustum)) {
            if (area.areaRenderer == null) {
              areaRendererPool.obtain().set(area);
            }
            modelBatch.render(area.areaRenderer, environment);
          } else if (area.areaRenderer != null) {
            areaRendererPool.free(area.areaRenderer);
          }
        }
      }
    }

    modelBatch.end();
  }

  public boolean areaInFrustum(Area area, Frustum frustum) {
    return frustum.boundsInFrustum(area.cenBlockX, area.cenBlockY, area.cenBlockZ, Area.HALF_SIZE_BLOCKS + 0.5f, Area.HALF_SIZE_BLOCKS + 0.5f, Area.HALF_SIZE_BLOCKS + 0.5f);
  }

  @Override
  public void dispose() {
    modelBatch.dispose();
  }

  public void free(AreaRenderer areaRenderer) {
    areaRendererPool.free(areaRenderer);
  }
}
