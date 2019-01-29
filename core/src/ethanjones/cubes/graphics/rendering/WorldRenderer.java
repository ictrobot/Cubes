package ethanjones.cubes.graphics.rendering;

import ethanjones.cubes.core.performance.Performance;
import ethanjones.cubes.core.performance.PerformanceTags;
import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.core.system.Pools;
import ethanjones.cubes.entity.Entity;
import ethanjones.cubes.graphics.Graphics;
import ethanjones.cubes.graphics.world.WorldGraphicsPools;
import ethanjones.cubes.graphics.world.area.AreaBoundaries;
import ethanjones.cubes.graphics.world.area.AreaRenderStatus;
import ethanjones.cubes.graphics.world.area.AreaRenderer;
import ethanjones.cubes.graphics.world.area.DebugLineRenderer;
import ethanjones.cubes.graphics.world.other.BreakingRenderer;
import ethanjones.cubes.graphics.world.other.RainRenderer;
import ethanjones.cubes.graphics.world.other.SelectedBlock;
import ethanjones.cubes.graphics.world.other.SunRenderer;
import ethanjones.cubes.input.CameraController;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.storage.Area;
import ethanjones.cubes.world.storage.AreaMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntSet;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static ethanjones.cubes.graphics.Graphics.modelBatch;
import static ethanjones.cubes.world.storage.Area.HALF_SIZE_BLOCKS;
import static ethanjones.cubes.world.storage.Area.SIZE_BLOCKS;

public class WorldRenderer implements Disposable {

  private static ArrayDeque<AreaNode> poolNode = new ArrayDeque<AreaNode>(256);

  public PerspectiveCamera camera;
  private ArrayList<AreaRenderer> needToRefresh = new ArrayList<AreaRenderer>();
  private ArrayDeque<AreaNode> queue = new ArrayDeque<AreaNode>();
  private IntSet checkedNodes = new IntSet(1024);
  private int effectiveViewDistance = 0;

  public WorldRenderer() {
    camera = new PerspectiveCamera(Settings.getIntegerSettingValue(Settings.GRAPHICS_FOV), Graphics.RENDER_WIDTH, Graphics.RENDER_HEIGHT) {
      @Override
      public void update(boolean b) {
        viewportWidth = Graphics.RENDER_WIDTH;
        viewportHeight = Graphics.RENDER_HEIGHT;
        super.update(b);
      }
    };
    camera.near = 0.1f;
    camera.far = Settings.getIntegerSettingValue(Settings.GRAPHICS_VIEW_DISTANCE) * SIZE_BLOCKS * 1.25f;

    Cubes.getClient().inputChain.cameraController = new CameraController(camera);
  }

  public void render() {
    WorldGraphicsPools.free();

    Performance.start(PerformanceTags.CLIENT_RENDER_WORLD);
    AreaRenderer.frameStart();

    needToRefresh.clear();
    queue.clear();
    checkedNodes.clear();
    effectiveViewDistance = 0;

    modelBatch.begin(camera);

    int renderDistance = Settings.getIntegerSettingValue(Settings.GRAPHICS_VIEW_DISTANCE);
    AreaBoundaries.update();

    Performance.start(PerformanceTags.CLIENT_RENDER_WORLD_AREAS);
    World world = Cubes.getClient().world;
    AreaMap areaMap = world.map;
    areaMap.lock.readLock();

    AreaReference pos = Pools.obtainAreaReference().setFromPositionVector3(Cubes.getClient().player.position);
    int yPos = CoordinateConverter.area(Cubes.getClient().player.position.y);

    AreaNode startingNode = get(areaMap.getArea(pos.areaX, pos.areaZ), pos.areaX, pos.areaZ, yPos);
    startingNode.firstNode = true; // Fix flashes caused by the starting area just being in frustum as flying up/down (e.g. Y=64.001) and areaInFrustum returning false
    queue.add(startingNode);

    boolean noClip = Cubes.getClient().player.noClip();

    while (!queue.isEmpty()) {
      AreaNode node = queue.pop();
      Area area = node.area;
      int ySection = node.ySection;
      int areaX = node.areaX;
      int areaZ = node.areaZ;
      int packedID = (areaX & 0x3FF) | ((areaZ & 0x3FF) << 10) | ((ySection & 0x3FF) << 20);
      int areaDistance = Math.max(Math.abs(areaX - pos.areaX), Math.abs(areaZ - pos.areaZ));

      if (!checkedNodes.add(packedID) || (!node.firstNode && (!areaInFrustum(areaX, areaZ, ySection, camera.frustum) || areaDistance > renderDistance))) {
        poolNode.add(node);
        continue;
      }

      boolean nullArea = area == null || ySection >= area.height;
      int traverse = 0;

      if (!nullArea && ySection >= 0) {
        area.lock.writeLock();

        int status = area.renderStatus[ySection];
        if (status == AreaRenderStatus.UNKNOWN) status = AreaRenderStatus.update(area, ySection);
        traverse = (noClip || status == AreaRenderStatus.EMPTY) ? 0 : status;
        boolean render = status != AreaRenderStatus.EMPTY && !complete(area, ySection, areaMap, status);

        if (render) {
          if (area.areaRenderer[ySection] == null) Pools.obtain(AreaRenderer.class).set(area, ySection);

          AreaRenderer areaRenderer = area.areaRenderer[ySection];
          if (areaRenderer.needsRefresh()) {
            needToRefresh.add(areaRenderer);
          } else {
            modelBatch.render(areaRenderer);
            renderIfNotNull(AreaBoundaries.drawArea(areaX, ySection, areaZ));
            if (areaDistance > effectiveViewDistance) effectiveViewDistance = areaDistance;
          }
        } else if (area.areaRenderer[ySection] != null) {
          AreaRenderer.free(area.areaRenderer[ySection]);
          area.areaRenderer[ySection] = null;
        }

        area.lock.writeUnlock();
      }

      if (traverse != AreaRenderStatus.COMPLETE) {
        Area a;
        if ((traverse & AreaRenderStatus.COMPLETE_MAX_X) == 0 && further(pos.areaX, pos.areaZ, areaX, areaZ, areaX + 1, areaZ)) {
          if ((a = areaMap.lockedGetArea(areaX + 1, areaZ)) != null) queue.add(get(a, areaX + 1, areaZ, ySection));
        }
        if ((traverse & AreaRenderStatus.COMPLETE_MIN_X) == 0 && further(pos.areaX, pos.areaZ, areaX, areaZ, areaX - 1, areaZ)) {
          if ((a = areaMap.lockedGetArea(areaX - 1, areaZ)) != null) queue.add(get(a, areaX - 1, areaZ, ySection));
        }
        if ((traverse & AreaRenderStatus.COMPLETE_MAX_Z) == 0 && further(pos.areaX, pos.areaZ, areaX, areaZ, areaX, areaZ + 1)) {
          if ((a = areaMap.lockedGetArea(areaX, areaZ + 1)) != null) queue.add(get(a, areaX, areaZ + 1, ySection));
        }
        if ((traverse & AreaRenderStatus.COMPLETE_MIN_Z) == 0 && further(pos.areaX, pos.areaZ, areaX, areaZ, areaX, areaZ - 1)) {
          if ((a = areaMap.lockedGetArea(areaX, areaZ - 1)) != null) queue.add(get(a, areaX, areaZ - 1, ySection));
        }
        if ((traverse & AreaRenderStatus.COMPLETE_MAX_Y) == 0 && !nullArea && ySection >= yPos) {
          queue.add(get(area, areaX, areaZ, ySection + 1));
        }
        if ((traverse & AreaRenderStatus.COMPLETE_MIN_Y) == 0 && ySection > 0 && ySection <= yPos) {
          queue.add(get(area, areaX, areaZ, ySection - 1));
        }
      }
      poolNode.add(node);
    }
    areaMap.lock.readUnlock();
    Performance.stop(PerformanceTags.CLIENT_RENDER_WORLD_AREAS);

    int refreshed = 0;
    if (needToRefresh.size() > 0) {
      Performance.start(PerformanceTags.CLIENT_RENDER_WORLD_UPDATES);
      Collections.sort(needToRefresh, new AreaRendererSorter());
      boolean doUpdates = true;
      for (AreaRenderer areaRenderer : needToRefresh) {
        if (doUpdates) {
          Performance.start(PerformanceTags.CLIENT_RENDER_WORLD_UPDATE);
          if (areaRenderer.update()) {
            refreshed++;
          }
          Performance.stop(PerformanceTags.CLIENT_RENDER_WORLD_UPDATE);

          if (refreshed > 0 && (System.nanoTime() - Cubes.getClient().frameStart) > 3000000) doUpdates = false;
        }
        modelBatch.render(areaRenderer);
      }
      Performance.stop(PerformanceTags.CLIENT_RENDER_WORLD_UPDATES);
    }
    AreaRenderer.refreshQueueLength = needToRefresh.size() - refreshed;

    Performance.start(PerformanceTags.CLIENT_RENDER_WORLD_ENTITY);
    float deltaTime = Gdx.graphics.getDeltaTime();
    world.entities.lock.readLock();
    for (Entity entity : world.entities.values()) {
      entity.updatePosition(deltaTime);
      if (entity instanceof RenderableProvider && entity.inFrustum(camera.frustum)) modelBatch.render(((RenderableProvider) entity));
    }
    world.entities.lock.readUnlock();
    Performance.stop(PerformanceTags.CLIENT_RENDER_WORLD_ENTITY);

    RainRenderer.draw(modelBatch);

    renderIfNotNull(SelectedBlock.draw());
    renderIfNotNull(BreakingRenderer.draw());
    renderIfNotNull(AreaBoundaries.drawCurrent(pos.areaX, yPos, pos.areaZ));
    SunRenderer.draw(modelBatch);

    Performance.start(PerformanceTags.CLIENT_RENDER_WORLD_FLUSH);
    modelBatch.end();
    Performance.stop(PerformanceTags.CLIENT_RENDER_WORLD_FLUSH);

    DebugLineRenderer.render();

    Performance.stop(PerformanceTags.CLIENT_RENDER_WORLD);
  }

  private void renderIfNotNull(Renderable r) {
    if (r != null) modelBatch.render(r);
  }

  public boolean areaInFrustum(Area area, Frustum frustum) {
    return frustum.boundsInFrustum(area.minBlockX + Area.HALF_SIZE_BLOCKS, Area.MAX_Y / 2f, area.minBlockZ + Area.HALF_SIZE_BLOCKS, Area.HALF_SIZE_BLOCKS, Area.MAX_Y / 2f, Area.HALF_SIZE_BLOCKS);
  }

  public boolean areaInFrustum(int areaX, int areaZ, int ySection, Frustum frustum) {
    return frustum.boundsInFrustum((areaX * Area.SIZE_BLOCKS) + Area.HALF_SIZE_BLOCKS, (ySection * Area.SIZE_BLOCKS) + Area.HALF_SIZE_BLOCKS, (areaZ * Area.SIZE_BLOCKS) + Area.HALF_SIZE_BLOCKS, Area.HALF_SIZE_BLOCKS, Area.HALF_SIZE_BLOCKS, Area.HALF_SIZE_BLOCKS);
  }

  public boolean complete(Area area, int ySection, AreaMap areaMap, int status) {
    if (status == AreaRenderStatus.COMPLETE) {
      if (ySection > 0 && (area.renderStatus[ySection - 1] & AreaRenderStatus.COMPLETE_MAX_Y) != AreaRenderStatus.COMPLETE_MAX_Y)
        return false;
      if (ySection < area.renderStatus.length - 1 && (area.renderStatus[ySection + 1] & AreaRenderStatus.COMPLETE_MIN_Y) != AreaRenderStatus.COMPLETE_MIN_Y)
        return false;
      Area maxX = areaMap.lockedGetArea(area.areaX + 1, area.areaZ);
      if (maxX != null && !maxX.isBlank())
        if (maxX.renderStatus.length < ySection - 1 || (maxX.renderStatus[ySection] & AreaRenderStatus.COMPLETE_MIN_X) != AreaRenderStatus.COMPLETE_MIN_X)
          return false;
      Area minX = areaMap.lockedGetArea(area.areaX - 1, area.areaZ);
      if (minX != null && !minX.isBlank())
        if (minX.renderStatus.length < ySection - 1 || (minX.renderStatus[ySection] & AreaRenderStatus.COMPLETE_MAX_X) != AreaRenderStatus.COMPLETE_MAX_X)
          return false;
      Area maxZ = areaMap.lockedGetArea(area.areaX, area.areaZ + 1);
      if (maxZ != null && !maxZ.isBlank())
        if (maxZ.renderStatus.length < ySection - 1 || (maxZ.renderStatus[ySection] & AreaRenderStatus.COMPLETE_MIN_Z) != AreaRenderStatus.COMPLETE_MIN_Z)
          return false;
      Area minZ = areaMap.lockedGetArea(area.areaX, area.areaZ - 1);
      if (minZ != null && !minZ.isBlank())
        if (minZ.renderStatus.length < ySection - 1 || (minZ.renderStatus[ySection] & AreaRenderStatus.COMPLETE_MAX_Z) != AreaRenderStatus.COMPLETE_MAX_Z)
          return false;
      return true;
    }
    return false;
  }

  private static boolean further(int posAreaX, int posAreaZ, int oldAreaX, int oldAreaZ, int newAreaX, int newAreaZ) {
    int oDX = Math.abs(posAreaX - oldAreaX);
    int oDZ = Math.abs(posAreaZ - oldAreaZ);

    int nDX = Math.abs(posAreaX - newAreaX);
    int nDZ = Math.abs(posAreaZ - newAreaZ);
    return (oDX * oDX + oDZ * oDZ) <= (nDX * nDX + nDZ * nDZ);
  }

  @Override
  public void dispose() {

  }

  private static AreaNode get(Area area, int areaX, int areaZ, int ySection) {
    AreaNode node = poolNode.pollFirst();
    if (node == null) node = new AreaNode();
    node.set(area, areaX, areaZ, ySection);
    return node;
  }

  private static class AreaNode {

    Area area;
    int areaX;
    int areaZ;
    int ySection;
    int hashCode;
    boolean firstNode;

    public void set(Area area, int areaX, int areaZ, int ySection) {
      this.area = area;
      this.areaX = areaX;
      this.areaZ = areaZ;
      this.ySection = ySection;
      firstNode = false;

      int hashCode = 7;
      hashCode = 31 * hashCode + ySection;
      hashCode = 31 * hashCode + areaX;
      this.hashCode = 31 * hashCode + areaZ;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof AreaNode) {
        AreaNode a = ((AreaNode) obj);
        return a.areaX == this.areaX && a.areaZ == this.areaZ && a.ySection == this.ySection;
      }
      return false;
    }

    @Override
    public int hashCode() {
      return hashCode;
    }
  }

  protected static class AreaRendererSorter implements Comparator<AreaRenderer> {

    public Vector3 base = Cubes.getClient().player.position;

    @Override
    public int compare(AreaRenderer o1, AreaRenderer o2) {
      Vector3 v1 = o1.getOffset();
      Vector3 v2 = o2.getOffset();

      float d1 = base.dst2(v1.x + HALF_SIZE_BLOCKS, (o1.getYSection() * SIZE_BLOCKS) + HALF_SIZE_BLOCKS, v1.z + HALF_SIZE_BLOCKS);
      float d2 = base.dst2(v2.x + HALF_SIZE_BLOCKS, (o2.getYSection() * SIZE_BLOCKS) + HALF_SIZE_BLOCKS, v2.z + HALF_SIZE_BLOCKS);

      final float dst = d1 - d2;
      final int result = dst < 0 ? -1 : (dst > 0 ? 1 : 0);
      return result;
    }
  }

  public int getEffectiveViewDistance() {
    return effectiveViewDistance;
  }
}
