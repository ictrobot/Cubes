package ethanjones.cubes.graphics.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.utils.Disposable;

import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.core.system.Pools;
import ethanjones.cubes.graphics.world.AreaRenderer;
import ethanjones.cubes.graphics.world.AreaRendererPool;
import ethanjones.cubes.input.CameraController;
import ethanjones.cubes.side.client.CubesClient;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.storage.Area;

import static ethanjones.cubes.graphics.Graphics.modelBatch;

public class WorldRenderer implements Disposable {

  public Environment environment;
  public PerspectiveCamera camera;

  static {
    Pools.registerType(AreaRenderer.class, new AreaRendererPool());
  }

  public WorldRenderer() {
    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.9f, -0.8f));

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

    Cubes.getClient().inputChain.cameraController = new CameraController(camera);
  }

  public void render() {
    modelBatch.begin(camera);

    int renderDistance = Settings.getIntegerSettingValue(Settings.GRAPHICS_VIEW_DISTANCE);

    AreaReference pos = Pools.obtainAreaReference().setFromPositionVector3(Cubes.getClient().player.position);
    int yPos = CoordinateConverter.area(Cubes.getClient().player.position.y);
    for (int areaX = pos.areaX - renderDistance; areaX <= pos.areaX + renderDistance; areaX++) {
      for (int areaZ = pos.areaZ - renderDistance; areaZ <= pos.areaZ + renderDistance; areaZ++) {
        Area area = CubesClient.getClient().world.getArea(areaX, areaZ);
        if (area == null || area.isBlank()) continue;
        if (!areaInFrustum(area, camera.frustum)) {
          AreaRenderer.free(area.areaRenderer);
          continue;
        }
        for (int ySection = Math.max(yPos - renderDistance, 0); ySection <= yPos + renderDistance; ySection++) {
          if ((((ySection + 1) * Area.SIZE_BLOCKS) - 1) > area.maxY) break;
          if (areaInFrustum(area, ySection, camera.frustum)) {
            if (area.areaRenderer[ySection] == null) {
              Pools.obtain(AreaRenderer.class).set(area, ySection);
            }
            modelBatch.render(area.areaRenderer[ySection], environment);
          } else if (area.areaRenderer[ySection] != null) {
            AreaRenderer.free(area.areaRenderer[ySection]);
          }
        }
      }
    }

    modelBatch.end();
  }

  public boolean areaInFrustum(Area area, Frustum frustum) {
    return frustum.boundsInFrustum(area.minBlockX + Area.HALF_SIZE_BLOCKS, Area.MAX_Y / 2f, area.minBlockZ + Area.HALF_SIZE_BLOCKS, Area.HALF_SIZE_BLOCKS, Area.MAX_Y / 2f, Area.HALF_SIZE_BLOCKS);
  }

  public boolean areaInFrustum(Area area, int ySection, Frustum frustum) {
    return frustum.boundsInFrustum(area.minBlockX + Area.HALF_SIZE_BLOCKS, (ySection * Area.SIZE_BLOCKS) + Area.HALF_SIZE_BLOCKS, area.minBlockZ + Area.HALF_SIZE_BLOCKS, Area.HALF_SIZE_BLOCKS, Area.HALF_SIZE_BLOCKS, Area.HALF_SIZE_BLOCKS);
  }

  @Override
  public void dispose() {

  }
}
