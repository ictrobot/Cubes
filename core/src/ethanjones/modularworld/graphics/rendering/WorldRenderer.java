package ethanjones.modularworld.graphics.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Frustum;
import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.core.settings.Settings;
import ethanjones.modularworld.core.thread.Threads;
import ethanjones.modularworld.graphics.world.AreaRenderableProvider;
import ethanjones.modularworld.world.reference.AreaReference;
import ethanjones.modularworld.world.storage.Area;

public class WorldRenderer {

  public static int RENDER_DISTANCE_MAX = 10;
  public static int RENDER_DISTANCE_MIN = 1;

  public Environment environment;
  public PerspectiveCamera camera;

  private Renderer renderer;
  private AreaRenderableProvider.AreaRenderableProviderPool areaRenderableProviderPool;

  public WorldRenderer(Renderer renderer) {
    this.renderer = renderer;

    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

    setupCamera();

    areaRenderableProviderPool = new AreaRenderableProvider.AreaRenderableProviderPool();
  }

  public void setupCamera() {
    camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.direction.x = 1;
    camera.direction.y = 0;
    camera.direction.z = 0;
    camera.near = 0.1f;
    camera.far = 300f;
  }

  public void render() {
    ModularWorld.instance.player.movementHandler.updateCamera(camera);
    camera.update(true);
    areaRenderableProviderPool.freeAll();

    int renderDistance = Settings.renderer_block_viewDistance.getIntegerSetting().getValue();

    AreaReference pos = ModularWorld.instance.world.playerArea;
    for (int areaX = pos.areaX - renderDistance; areaX <= pos.areaX + renderDistance; areaX++) {
      for (int areaY = pos.areaY - renderDistance; areaY <= pos.areaY + renderDistance; areaY++) {
        for (int areaZ = pos.areaZ - renderDistance; areaZ <= pos.areaZ + renderDistance; areaZ++) {
          if (areaY < 0) {
            continue;
          }
          Area area = ModularWorld.instance.world.getArea(areaX, areaY, areaZ);
          if (area.areaRenderer == null) {
            continue;
          }
          if (!areaInFrustum(area, camera.frustum)) {
            continue;
          }
          if (area.areaRendererFuture == null || (area.areaRendererFuture.isDone() && area.areaRenderer.isDirty())) {
            area.areaRendererFuture = Threads.execute(area.areaRenderer);
          }
          renderer.gameBatch.render(areaRenderableProviderPool.obtain().setFuture(area.areaRendererFuture), environment);
        }
      }
    }
  }

  public boolean areaInFrustum(Area area, Frustum frustum) {
    return frustum.boundsInFrustum(area.cenBlockX, area.cenBlockY, area.cenBlockZ, Area.HALF_SIZE_BLOCKS + 0.5f, Area.HALF_SIZE_BLOCKS + 0.5f, Area.HALF_SIZE_BLOCKS + 0.5f);
  }
}
