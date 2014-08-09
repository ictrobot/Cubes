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
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.world.WorldClient;
import ethanjones.modularworld.world.reference.AreaReference;
import ethanjones.modularworld.world.storage.Area;

public class BlockRenderer implements Disposable {

  public static int RENDER_DISTANCE_MAX = 10;
  public static int RENDER_DISTANCE_MIN = 1;

  public Environment environment;
  public PerspectiveCamera camera;

  private ModelBatch modelBatch;

  public BlockRenderer() {
    modelBatch = new ModelBatch();

    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

    setupCamera();

  }

  public void setupCamera() {
    camera = new PerspectiveCamera(Settings.input_fieldOfView.getIntegerSetting().getValue(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.direction.x = 1;
    camera.direction.y = 0;
    camera.direction.z = 0;
    camera.near = 0.1f;
    camera.far = 300f;
  }

  public void render() {
    modelBatch.begin(camera);

    ModularWorldClient.instance.player.movementHandler.updateCamera(camera);
    camera.update(true);

    int renderDistance = Settings.renderer_block_viewDistance.getIntegerSetting().getValue();

    AreaReference pos = ((WorldClient) ModularWorldClient.instance.world).playerArea;
    for (int areaX = pos.areaX - renderDistance; areaX <= pos.areaX + renderDistance; areaX++) {
      for (int areaY = Math.max(pos.areaY - renderDistance, 0); areaY <= pos.areaY + renderDistance; areaY++) {
        for (int areaZ = pos.areaZ - renderDistance; areaZ <= pos.areaZ + renderDistance; areaZ++) {
          Area area = ModularWorldClient.instance.world.getArea(areaX, areaY, areaZ);
          if (area.areaRenderer == null) continue;
          if (!areaInFrustum(area, camera.frustum)) continue;
          modelBatch.render(area.areaRenderer, environment);
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
}
