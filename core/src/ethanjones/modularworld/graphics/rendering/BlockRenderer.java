package ethanjones.modularworld.graphics.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.block.Block;
import ethanjones.modularworld.core.debug.Debug;
import ethanjones.modularworld.core.settings.Settings;
import ethanjones.modularworld.world.coordinates.AreaCoordinates;
import ethanjones.modularworld.world.coordinates.BlockCoordinates;
import ethanjones.modularworld.world.storage.Area;

public class BlockRenderer {

  public static int RENDER_DISTANCE_MAX = 10;
  public static int RENDER_DISTANCE_MIN = 1;

  public Environment lights;
  public PerspectiveCamera camera;

  private Renderer renderer;

  public BlockRenderer(Renderer renderer) {
    this.renderer = renderer;

    lights = new Environment();
    lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
    lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

    setupCamera();

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

    int renderDistance = Settings.renderer_block_viewDistance.getIntegerSetting().getValue();

    long l = System.currentTimeMillis();
    int rendered = 0;
    int renderedChunks = 0;
    int totalChunks = 0;
    BlockCoordinates pos = new BlockCoordinates(ModularWorld.instance.player.position.x, ModularWorld.instance.player.position.y, ModularWorld.instance.player.position.z);
    for (int areaX = pos.areaX - renderDistance; areaX <= pos.areaX + renderDistance; areaX++) {
      for (int areaY = pos.areaY - renderDistance; areaY <= pos.areaY + renderDistance; areaY++) {
        for (int areaZ = pos.areaZ - renderDistance; areaZ <= pos.areaZ + renderDistance; areaZ++) {
          if (areaY < 0) {
            continue;
          }
          totalChunks++;
          Area area = ModularWorld.instance.world.getArea(new AreaCoordinates(areaX, areaY, areaZ));
          if (!areaInFrustum(area, camera.frustum)) {
            continue;
          }
          renderedChunks++;
          for (int x = 0; x < Area.S; x++) {
            for (int y = 0; y < Area.S; y++) {
              for (int z = 0; z < Area.S; z++) {
                Block b = area.getBlock(x, y, z);
                if (b != null) {
                  rendered += b.getRenderer().render(renderer.modelBatch, lights, camera, x + area.minBlockX, y + area.minBlockY, z + area.minBlockZ);
                }
              }
            }
          }
        }
      }
    }
    long t = System.currentTimeMillis() - l;
    Debug.blockRenderer(t, rendered, renderedChunks, totalChunks);
  }

  public boolean areaInFrustum(Area area, Frustum frustum) {
    return frustum.boundsInFrustum(new BoundingBox(new Vector3(area.minBlockX, area.minBlockY, area.minBlockZ), new Vector3(area.maxBlockX + 1, area.maxBlockY + 1, area.maxBlockZ + 1)));
  }
}
