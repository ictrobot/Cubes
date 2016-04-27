package ethanjones.cubes.graphics.rendering;

import ethanjones.cubes.core.performance.Performance;
import ethanjones.cubes.core.performance.PerformanceTags;
import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.core.system.Pools;
import ethanjones.cubes.entity.Entity;
import ethanjones.cubes.graphics.world.*;
import ethanjones.cubes.input.CameraController;
import ethanjones.cubes.side.client.CubesClient;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.client.WorldClient;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.storage.Area;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;

import java.util.ArrayList;
import java.util.Collections;

import static ethanjones.cubes.graphics.Graphics.modelBatch;

public class WorldRenderer implements Disposable {

  static {
    Pools.registerType(AreaRenderer.class, new Pool<AreaRenderer>() {
      @Override
      protected AreaRenderer newObject() {
        return new AreaRenderer();
      }
    });
    Pools.registerType(AreaMesh.class, new Pool<AreaMesh>() {
      @Override
      protected AreaMesh newObject() {
        return new AreaMesh();
      }
    });
  }

  public PerspectiveCamera camera;
  private AreaReference fastGet = new AreaReference();

  private ArrayList<AreaRenderer> needToRefresh = new ArrayList<AreaRenderer>();

  public WorldRenderer() {
    camera = new PerspectiveCamera(Settings.getIntegerSettingValue(Settings.GRAPHICS_FOV), Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) {
      @Override
      public void update(boolean b) {
        viewportWidth = Gdx.graphics.getWidth();
        viewportHeight = Gdx.graphics.getHeight();
        super.update(b);
      }
    };
    camera.near = 0.2f;
    camera.far = 768f;

    Cubes.getClient().inputChain.cameraController = new CameraController(camera);
  }

  public void render() {
    Performance.start(PerformanceTags.CLIENT_RENDER_WORLD);
    needToRefresh.clear();
    modelBatch.begin(camera);

    int renderDistance = Settings.getIntegerSettingValue(Settings.GRAPHICS_VIEW_DISTANCE);

    Performance.start(PerformanceTags.CLIENT_RENDER_WORLD_AREAS);
    WorldClient world = (WorldClient) CubesClient.getClient().world;
    world.lock.readLock();
    AreaReference pos = Pools.obtainAreaReference().setFromPositionVector3(Cubes.getClient().player.position);
    int yPos = CoordinateConverter.area(Cubes.getClient().player.position.y);
    for (int areaX = pos.areaX - renderDistance; areaX <= pos.areaX + renderDistance; areaX++) {
      for (int areaZ = pos.areaZ - renderDistance; areaZ <= pos.areaZ + renderDistance; areaZ++) {
        Area area = fastGet(world, areaX, areaZ);
        if (area == null || area.isBlank()) continue;
        if (!areaInFrustum(area, camera.frustum)) {
          AreaRenderer.free(area.areaRenderer);
          continue;
        }
        for (int ySection = Math.max(yPos - renderDistance, 0); ySection <= yPos + renderDistance; ySection++) {
          if (ySection >= area.height) break;
          if (shouldRender(world, area, ySection)) {
            if (areaInFrustum(area, ySection, camera.frustum)) {
              if (area.areaRenderer[ySection] == null) {
                Pools.obtain(AreaRenderer.class).set(area, ySection);
              }
              // add if ready, else add to update queue
              AreaRenderer areaRenderer = area.areaRenderer[ySection];
              if (areaRenderer.needsRefresh()) {
                needToRefresh.add(areaRenderer);
              } else {
                modelBatch.render(areaRenderer);
              }
            } // don't free areas not in frustum
          } else if (area.areaRenderer[ySection] != null) {
            AreaRenderer.free(area.areaRenderer[ySection]);
            area.areaRenderer[ySection] = null;
          }
        }
      }
    }
    Performance.stop(PerformanceTags.CLIENT_RENDER_WORLD_AREAS);

    if (needToRefresh.size() > 0) {
      Performance.start(PerformanceTags.CLIENT_RENDER_WORLD_UPDATES);
      Collections.sort(needToRefresh, new AreaRendererSorter());
      for (AreaRenderer areaRenderer : needToRefresh) {
        Performance.start(PerformanceTags.CLIENT_RENDER_WORLD_UPDATE);
        modelBatch.render(areaRenderer);
        Performance.stop(PerformanceTags.CLIENT_RENDER_WORLD_UPDATE);
      }
      Performance.stop(PerformanceTags.CLIENT_RENDER_WORLD_UPDATES);
    }

    Performance.start(PerformanceTags.CLIENT_RENDER_WORLD_ENTITY);
    for (Entity entity : world.entities.values()) {
      if (entity instanceof RenderableProvider) modelBatch.render(((RenderableProvider) entity));
    }
    Performance.stop(PerformanceTags.CLIENT_RENDER_WORLD_ENTITY);

    Renderable selected = SelectedBlock.draw();
    if (selected != null) modelBatch.render(selected);
    SunRenderer.draw(modelBatch);

    world.lock.readUnlock();
    modelBatch.end();

    Performance.stop(PerformanceTags.CLIENT_RENDER_WORLD);
  }

  public boolean areaInFrustum(Area area, Frustum frustum) {
    return frustum.boundsInFrustum(area.minBlockX + Area.HALF_SIZE_BLOCKS, Area.MAX_Y / 2f, area.minBlockZ + Area.HALF_SIZE_BLOCKS, Area.HALF_SIZE_BLOCKS, Area.MAX_Y / 2f, Area.HALF_SIZE_BLOCKS);
  }

  public boolean areaInFrustum(Area area, int ySection, Frustum frustum) {
    return frustum.boundsInFrustum(area.minBlockX + Area.HALF_SIZE_BLOCKS, (ySection * Area.SIZE_BLOCKS) + Area.HALF_SIZE_BLOCKS, area.minBlockZ + Area.HALF_SIZE_BLOCKS, Area.HALF_SIZE_BLOCKS, Area.HALF_SIZE_BLOCKS, Area.HALF_SIZE_BLOCKS);
  }

  public boolean shouldRender(WorldClient world, Area area, int ySection) {
    int status = area.renderStatus[ySection];
    if (status == AreaRenderStatus.UNKNOWN) status = AreaRenderStatus.update(area, ySection);
    if (status == AreaRenderStatus.EMPTY) return false;
    if (status == AreaRenderStatus.COMPLETE) {
      if (ySection > 0 && (area.renderStatus[ySection - 1] & AreaRenderStatus.COMPLETE_MAX_Y) != AreaRenderStatus.COMPLETE_MAX_Y)
        return true;
      if (ySection < area.renderStatus.length - 1 && (area.renderStatus[ySection + 1] & AreaRenderStatus.COMPLETE_MIN_Y) != AreaRenderStatus.COMPLETE_MIN_Y)
        return true;
      Area maxX = fastGet(world, area.areaX + 1, area.areaZ);
      if (maxX != null && !maxX.isBlank())
        if (maxX.renderStatus.length < ySection - 1 || (maxX.renderStatus[ySection] & AreaRenderStatus.COMPLETE_MIN_X) != AreaRenderStatus.COMPLETE_MIN_X)
          return true;
      Area minX = fastGet(world, area.areaX - 1, area.areaZ);
      if (minX != null && !minX.isBlank())
        if (minX.renderStatus.length < ySection - 1 || (minX.renderStatus[ySection] & AreaRenderStatus.COMPLETE_MAX_X) != AreaRenderStatus.COMPLETE_MAX_X)
          return true;
      Area maxZ = fastGet(world, area.areaX, area.areaZ + 1);
      if (maxZ != null && !maxZ.isBlank())
        if (maxZ.renderStatus.length < ySection - 1 || (maxZ.renderStatus[ySection] & AreaRenderStatus.COMPLETE_MIN_Z) != AreaRenderStatus.COMPLETE_MIN_Z)
          return true;
      Area minZ = fastGet(world, area.areaX, area.areaZ - 1);
      if (minZ != null && !minZ.isBlank())
        if (minZ.renderStatus.length < ySection - 1 || (minZ.renderStatus[ySection] & AreaRenderStatus.COMPLETE_MAX_Z) != AreaRenderStatus.COMPLETE_MAX_Z)
          return true;
      return false;
    }
    return true;
  }

  private Area fastGet(WorldClient worldClient, int x, int z) {
    fastGet.setFromAreaCoordinates(x, z);
    return worldClient.map.get(fastGet);
  }

  @Override
  public void dispose() {

  }
}
