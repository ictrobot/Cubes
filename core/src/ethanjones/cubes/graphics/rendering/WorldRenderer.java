package ethanjones.cubes.graphics.rendering;

import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.core.system.Pools;
import ethanjones.cubes.entity.Entity;
import ethanjones.cubes.graphics.world.*;
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
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import static ethanjones.cubes.graphics.Graphics.modelBatch;

public class WorldRenderer implements Disposable {

  private static ArrayDeque<AreaNode> poolNode = new ArrayDeque<AreaNode>();

  public PerspectiveCamera camera;
  private ArrayList<AreaRenderer> needToRefresh = new ArrayList<AreaRenderer>();
  private ArrayDeque<AreaNode> queue = new ArrayDeque<AreaNode>();
  private HashSet<AreaNode> checkedNodes = new HashSet<AreaNode>();

  public WorldRenderer() {
    camera = new PerspectiveCamera(Settings.getIntegerSettingValue(Settings.GRAPHICS_FOV), Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) {
      @Override
      public void update(boolean b) {
        viewportWidth = Gdx.graphics.getWidth();
        viewportHeight = Gdx.graphics.getHeight();
        super.update(b);
      }
    };
    camera.near = 0.1f;
    camera.far = 768f;

    Cubes.getClient().inputChain.cameraController = new CameraController(camera);
  }
  
  public void render() {
    WorldGraphicsPools.free();

    AreaRenderer.frameStart();

    needToRefresh.clear();
    queue.clear();
    checkedNodes.clear();
    
    modelBatch.begin(camera);

    int renderDistance = Settings.getIntegerSettingValue(Settings.GRAPHICS_VIEW_DISTANCE);
    AreaBoundaries.update();

    World world = Cubes.getClient().world;
    AreaMap areaMap = world.map;
    areaMap.lock.readLock();

    AreaReference pos = Pools.obtainAreaReference().setFromPositionVector3(Cubes.getClient().player.position);
    int yPos = CoordinateConverter.area(Cubes.getClient().player.position.y);

    AreaNode startingNode = get(areaMap.getArea(pos.areaX, pos.areaZ), pos.areaX, pos.areaZ, yPos);
    startingNode.firstNode = true; // Fix flashes caused by the starting area just being in frustum as flying up/down (e.g. Y=64.001) and areaInFrustum returning false
    queue.add(startingNode);

    while (!queue.isEmpty()) {
      AreaNode node = queue.pop();
      Area area = node.area;
      int ySection = node.ySection;
      int areaX = node.areaX;
      int areaZ = node.areaZ;

      if (!checkedNodes.add(node)) {
        poolNode.add(node);
        continue;
      }

      if (!node.firstNode) {
        if (!areaInFrustum(areaX, areaZ, ySection, camera.frustum)) continue;
        if (!inRange(areaX, areaZ, pos.areaX, pos.areaZ, renderDistance)) continue;
      }

      boolean nullArea = area == null || ySection >= area.height;
      int traverse = 0;

      if (!nullArea && ySection >= 0) {
        area.lock.writeLock();

        int status = area.renderStatus[ySection];
        if (status == AreaRenderStatus.UNKNOWN) status = AreaRenderStatus.update(area, ySection);
        traverse = status == AreaRenderStatus.EMPTY ? 0 : status;
        boolean render = status != AreaRenderStatus.EMPTY && !complete(area, ySection, areaMap, status);

        if (render) {
          if (area.areaRenderer[ySection] == null) Pools.obtain(AreaRenderer.class).set(area, ySection);

          AreaRenderer areaRenderer = area.areaRenderer[ySection];
          if (areaRenderer.needsRefresh()) {
            needToRefresh.add(areaRenderer);
          } else {
            modelBatch.render(areaRenderer);
            renderIfNotNull(AreaBoundaries.drawArea(areaX, ySection, areaZ));
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
    }
    areaMap.lock.readUnlock();
    poolNode.addAll(checkedNodes);

    if (needToRefresh.size() > 0) {
      Collections.sort(needToRefresh, new AreaRendererSorter());
      for (AreaRenderer areaRenderer : needToRefresh) {
        modelBatch.render(areaRenderer);
      }
    }

    float deltaTime = Gdx.graphics.getDeltaTime();
    world.entities.lock.readLock();
    for (Entity entity : world.entities.values()) {
      entity.updatePosition(deltaTime);
      if (entity instanceof RenderableProvider) modelBatch.render(((RenderableProvider) entity));
    }
    world.entities.lock.readUnlock();

    renderIfNotNull(SelectedBlock.draw());
    renderIfNotNull(BreakingRenderer.draw());
    renderIfNotNull(AreaBoundaries.drawCurrent(pos.areaX, yPos, pos.areaZ));
    SunRenderer.draw(modelBatch);
    modelBatch.end();
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
        return minZ.renderStatus.length >= ySection - 1 && (minZ.renderStatus[ySection] & AreaRenderStatus.COMPLETE_MAX_Z) == AreaRenderStatus.COMPLETE_MAX_Z;
      return true;
    }
    return false;
  }

  private static boolean inRange(int areaX, int areaZ, int posAreaX, int posAreaZ, int renderDistance) {
    return Math.abs(areaX - posAreaX) <= renderDistance && Math.abs(areaZ - posAreaZ) <= renderDistance;
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
}
