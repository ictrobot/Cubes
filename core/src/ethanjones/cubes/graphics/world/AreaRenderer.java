package ethanjones.cubes.graphics.world;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.block.BlockRenderType;
import ethanjones.cubes.core.id.IDManager;
import ethanjones.cubes.core.system.Pools;
import ethanjones.cubes.core.util.Lock;
import ethanjones.cubes.graphics.world.ao.AmbientOcclusion;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.common.Side;
import ethanjones.cubes.world.storage.Area;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;

import java.util.ArrayList;

import static ethanjones.cubes.graphics.world.AreaMesh.SAFE_VERTICES;
import static ethanjones.cubes.world.storage.Area.*;

public class AreaRenderer implements RenderableProvider, Disposable, Pool.Poolable {
  
  public static int renderedThisFrame = 0;
  public static int renderedMeshesThisFrame = 0;
  
  public boolean refresh = true;
  Vector3 offset = new Vector3();
  private Area area;
  private int ySection;
  private ArrayList<AreaMesh> meshs = new ArrayList<AreaMesh>();
  
  public boolean needsRefresh() {
    return refresh;
  }
  
  public boolean update() {
    if (refresh) {
      if ((System.nanoTime() - Cubes.getClient().frameStart) < 3000000 && calculateVertices()) {
        refresh = false;
        return true;
      } else {
        return meshs.size() > 0; //still render old meshs
      }
    }
    return true; // true indicates this area can be rendered
  }
  
  @Override
  public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
    if (area == null || !update() || meshs.size() == 0) return;
    renderedThisFrame++;
    for (AreaMesh mesh : meshs) {
      renderedMeshesThisFrame++;
      renderables.add(mesh.renderable(pool));
    }
  }
  
  public boolean calculateVertices() {
    if (area == null) return false;

    boolean ao = AmbientOcclusion.isEnabled();

    Area maxX = area.neighbour(area.areaX + 1, area.areaZ);
    Area minX = area.neighbour(area.areaX - 1, area.areaZ);
    Area maxZ = area.neighbour(area.areaX, area.areaZ + 1);
    Area minZ = area.neighbour(area.areaX, area.areaZ - 1);
    if (maxX == null || minX == null || maxZ == null || minZ == null) return false;
    
    free(meshs);
    
    if (maxX.isBlank()) maxX = null;
    if (minX.isBlank()) minX = null;
    if (maxZ.isBlank()) maxZ = null;
    if (minZ.isBlank()) minZ = null;
    
    int i = ySection * SIZE_BLOCKS_CUBED;
    int vertexOffset = 0;
    
    Lock.waitToLockAll(false, area, minX, maxX, minZ, maxZ);

    if (!area.isBlank()) {
      for (int y = ySection * SIZE_BLOCKS; y < (ySection + 1) * SIZE_BLOCKS; y++) {
        for (int z = 0; z < SIZE_BLOCKS; z++) {
          for (int x = 0; x < SIZE_BLOCKS; x++, i++) {
            int blockInt = area.blocks[i];
            if ((blockInt & BLOCK_VISIBLE) == BLOCK_VISIBLE) {
              vertexOffset = render(vertexOffset, blockInt, x, y, z, i, ao, maxX, minX, maxZ, minZ);
            }
          }
        }
      }
      if (vertexOffset > 0) {
        save(vertexOffset);
      }
    }
    
    area.lock.readUnlock();
    if (maxX != null) maxX.lock.readUnlock();
    if (minX != null) minX.lock.readUnlock();
    if (maxZ != null) maxZ.lock.readUnlock();
    if (minZ != null) minZ.lock.readUnlock();
    return true;
  }
  
  private int render(int vertexOffset, int blockInt, int x, int y, int z, int i, boolean ao, Area maxX, Area minX, Area maxZ, Area minZ) {
    Block block = IDManager.toBlock(blockInt & 0xFFFFF);
    if (block != null) {
      int meta = (blockInt >> 20) & 0xFF;
      BlockRenderType renderType = block.renderType(meta);
      BlockTextureHandler textureHandler = block.getTextureHandler(meta);
      vertexOffset = renderType.render(AreaMesh.vertices, vertexOffset, offset, block, meta, textureHandler, area, x, y, z, i, ao, minX, maxZ, minZ, maxX);
      
      if (vertexOffset >= SAFE_VERTICES) {
        save(vertexOffset);
        return 0;
      }
    }
    return vertexOffset;
  }
  
  private void save(int vertexCount) {
    AreaMesh areaMesh = Pools.obtain(AreaMesh.class);
    areaMesh.saveVertices(vertexCount);
    meshs.add(areaMesh);
  }
  
  public Vector3 getOffset() {
    if (area == null) return Vector3.Zero;
    return offset;
  }
  
  public int getYSection() {
    if (area == null) return 0;
    return ySection;
  }
  
  @Override
  public void dispose() {
    free(meshs);
  }
  
  @Override
  public void reset() {
    if (area != null && area.areaRenderer != null) area.areaRenderer[ySection] = null;
    free(meshs);
    area = null;
    refresh = true;
  }
  
  public AreaRenderer set(Area area, int ySection) {
    this.area = area;
    this.ySection = ySection;
    this.area.areaRenderer[ySection] = this;
    this.refresh = true;
    this.offset.set(area.minBlockX, 0, area.minBlockZ);
    return this;
  }
  
  // always client
  public static void free(AreaRenderer areaRenderer) {
    if (areaRenderer != null) Pools.free(AreaRenderer.class, areaRenderer);
  }
  
  // checks if client thread
  public static void free(AreaRenderer[] areaRenderer) {
    if (areaRenderer == null) return;
    boolean isClient = Side.isMainThread(Side.Client);
    for (int i = 0; i < areaRenderer.length; i++) {
      if (areaRenderer[i] == null) continue;
      if (isClient) {
        Pools.free(AreaRenderer.class, areaRenderer[i]);
      } else {
        WorldGraphicsPools.toFree.add(areaRenderer[i]);
      }
      areaRenderer[i] = null;
    }
  }
  
  public static void free(ArrayList<AreaMesh> areaMeshs) {
    if (areaMeshs == null) return;
    for (AreaMesh areaMesh : areaMeshs) {
      if (areaMesh == null) continue;
      Pools.free(AreaMesh.class, areaMesh);
    }
    areaMeshs.clear();
  }
}
