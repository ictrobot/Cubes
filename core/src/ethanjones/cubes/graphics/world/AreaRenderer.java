package ethanjones.cubes.graphics.world;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.util.BlockFace;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.storage.Area;

import static ethanjones.cubes.graphics.world.FaceVertices.*;
import static ethanjones.cubes.world.storage.Area.*;

public class AreaRenderer implements RenderableProvider, Disposable, Pool.Poolable {

  public static final VertexAttributes vertexAttributes = MeshBuilder.createAttributes(VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);

  public static final int MAX_X_OFFSET = 1;
  public static final int MIN_X_OFFSET = -MAX_X_OFFSET;
  public static final int MAX_Y_OFFSET = SIZE_BLOCKS_SQUARED;
  public static final int MIN_Y_OFFSET = -MAX_Y_OFFSET;
  public static final int MAX_Z_OFFSET = SIZE_BLOCKS;
  public static final int MIN_Z_OFFSET = -MAX_Z_OFFSET;

  public static final int MIN_AREA = 0;
  public static final int MAX_AREA = SIZE_BLOCKS - 1;

  public static final int VERTEX_SIZE = 8; //3 for position, 3 for normal, 2 for texture coordinates;

  private static short[] indices;
  private static float vertices[];

  static {
    int len = SIZE_BLOCKS_CUBED * 6 * 2; // 6 / 3
    indices = new short[len];
    short j = 0;
    for (int i = 0; i < len; i += 6, j += 4) {
      indices[i + 0] = (short) (j + 0);
      indices[i + 1] = (short) (j + 1);
      indices[i + 2] = (short) (j + 2);
      indices[i + 3] = (short) (j + 2);
      indices[i + 4] = (short) (j + 3);
      indices[i + 5] = (short) (j + 0);
    }

    vertices = new float[VERTEX_SIZE * 6 * SIZE_BLOCKS_CUBED];
  }

  public Mesh mesh;
  public boolean refresh = true;
  Vector3 offset = new Vector3();
  private int numVertices = 0;
  private Area area;

  protected AreaRenderer() {
    mesh = new Mesh(true, vertices.length, indices.length, vertexAttributes);
    mesh.setIndices(indices);
  }

  @Override
  public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
    if (area == null) return;
    if (refresh) {
      int numVerts = calculateVertices(vertices);
      numVertices = numVerts / 4 * 6;
      mesh.setVertices(vertices, 0, numVerts * VERTEX_SIZE);
      refresh = false;
    }
    if (numVertices <= 0) return;
    Renderable renderable = pool.obtain();
    renderable.material = Assets.blockPackedTextureSheet.getMaterial();
    renderable.mesh = mesh;
    renderable.meshPartOffset = 0;
    renderable.meshPartSize = numVertices;
    renderable.primitiveType = GL20.GL_TRIANGLES;
    renderables.add(renderable);
  }

  public int calculateVertices(float[] vertices) {
    if (area == null) return 0;

    Area maxX = Cubes.getClient().world.getArea(area.x + 1, area.y, area.z);
    Area minX = Cubes.getClient().world.getArea(area.x - 1, area.y, area.z);
    Area maxY = Cubes.getClient().world.getArea(area.x, area.y + 1, area.z);
    Area minY = Cubes.getClient().world.getArea(area.x, area.y - 1, area.z);
    Area maxZ = Cubes.getClient().world.getArea(area.x, area.y, area.z + 1);
    Area minZ = Cubes.getClient().world.getArea(area.x, area.y, area.z - 1);

    int i = 0;
    int vertexOffset = 0;
    synchronized (area) {
      for (int y = 0; y < SIZE_BLOCKS; y++) {
        for (int z = 0; z < SIZE_BLOCKS; z++) {
          for (int x = 0; x < SIZE_BLOCKS; x++, i++) {
            Block block = Sided.getBlockManager().toBlock(area.blockFactories[i]);
            if (block == null) continue;
            BlockTextureHandler textureHandler = block.getTextureHandler(area.blockData[i]);
            if (x < SIZE_BLOCKS - 1) {
              if (area.blockFactories[i + MAX_X_OFFSET] == 0) {
                vertexOffset = createMaxX(offset, textureHandler.getSide(BlockFace.posX).textureRegion, x, y, z, vertices, vertexOffset);
              }
            } else if (maxX == null || maxX.getBlock(MIN_AREA, y, z) == null) {
              vertexOffset = createMaxX(offset, textureHandler.getSide(BlockFace.posX).textureRegion, x, y, z, vertices, vertexOffset);
            }
            if (x > 0) {
              if (area.blockFactories[i + MIN_X_OFFSET] == 0) {
                vertexOffset = createMinX(offset, textureHandler.getSide(BlockFace.negX).textureRegion, x, y, z, vertices, vertexOffset);
              }
            } else if (minX == null || minX.getBlock(MAX_AREA, y, z) == null) {
              vertexOffset = createMinX(offset, textureHandler.getSide(BlockFace.negX).textureRegion, x, y, z, vertices, vertexOffset);
            }
            if (y < SIZE_BLOCKS - 1) {
              if (area.blockFactories[i + MAX_Y_OFFSET] == 0) {
                vertexOffset = createMaxY(offset, textureHandler.getSide(BlockFace.posY).textureRegion, x, y, z, vertices, vertexOffset);
              }
            } else if (maxY == null || maxY.getBlock(x, MIN_AREA, z) == null) {
              vertexOffset = createMaxY(offset, textureHandler.getSide(BlockFace.posY).textureRegion, x, y, z, vertices, vertexOffset);
            }
            if (y > 0) {
              if (area.blockFactories[i + MIN_Y_OFFSET] == 0) {
                vertexOffset = createMinY(offset, textureHandler.getSide(BlockFace.negY).textureRegion, x, y, z, vertices, vertexOffset);
              }
            } else if (minY == null || minY.getBlock(x, MAX_AREA, z) == null) {
              vertexOffset = createMinY(offset, textureHandler.getSide(BlockFace.negY).textureRegion, x, y, z, vertices, vertexOffset);
            }
            if (z < SIZE_BLOCKS - 1) {
              if (area.blockFactories[i + MAX_Z_OFFSET] == 0) {
                vertexOffset = createMaxZ(offset, textureHandler.getSide(BlockFace.posZ).textureRegion, x, y, z, vertices, vertexOffset);
              }
            } else if (maxZ == null || maxZ.getBlock(x, y, MIN_AREA) == null) {
              vertexOffset = createMaxZ(offset, textureHandler.getSide(BlockFace.posZ).textureRegion, x, y, z, vertices, vertexOffset);
            }
            if (z > 0) {
              if (area.blockFactories[i + MIN_Z_OFFSET] == 0) {
                vertexOffset = createMinZ(offset, textureHandler.getSide(BlockFace.negZ).textureRegion, x, y, z, vertices, vertexOffset);
              }
            } else if (minZ == null || minZ.getBlock(x, y, MAX_AREA) == null) {
              vertexOffset = createMinZ(offset, textureHandler.getSide(BlockFace.negZ).textureRegion, x, y, z, vertices, vertexOffset);
            }
          }
        }
      }
    }
    return vertexOffset / VERTEX_SIZE;
  }

  @Override
  public void dispose() {
    mesh.dispose();
  }

  @Override
  public void reset() {
    if (area != null) area.areaRenderer = null;
    area = null;
    refresh = true;
  }

  public AreaRenderer set(Area area) {
    this.area = area;
    this.area.areaRenderer = this;
    this.refresh = true;
    this.offset.set(area.minBlockX, area.minBlockY, area.minBlockZ);
    return this;
  }
}
