package ethanjones.modularworld.graphics.world;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import ethanjones.modularworld.block.Block;
import ethanjones.modularworld.core.util.Direction;
import ethanjones.modularworld.graphics.GraphicsHelper;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.world.storage.Area;

import static ethanjones.modularworld.graphics.world.FaceVertices.*;
import static ethanjones.modularworld.world.storage.Area.SIZE_BLOCKS;
import static ethanjones.modularworld.world.storage.Area.SIZE_BLOCKS_CUBED;

public class AreaRenderer implements RenderableProvider, Disposable {

  public static final int MAX_X_OFFSET = 1;
  public static final int MIN_X_OFFSET = -MAX_X_OFFSET;
  public static final int MAX_Y_OFFSET = SIZE_BLOCKS * SIZE_BLOCKS;
  public static final int MIN_Y_OFFSET = -MAX_Y_OFFSET;
  public static final int MAX_Z_OFFSET = SIZE_BLOCKS;
  public static final int MIN_Z_OFFSET = -MAX_Z_OFFSET;

  public static final int VERTEX_SIZE = 8; //3 for position, 3 for normal, 2 for texture coordinates;

  private static short[] indices;
  private static float vertices[];

  static {
    int len = SIZE_BLOCKS_CUBED * 6 * 6 / 3;
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
  public boolean dirty = true;
  Vector3 offset = new Vector3();
  private int numVertices = 0;
  private Area area;

  public AreaRenderer(Area area) {
    this.area = area;
    this.offset.set(area.minBlockX, area.minBlockY, area.minBlockZ);
    mesh = new Mesh(true, vertices.length, indices.length, GraphicsHelper.vertexAttributes);
    mesh.setIndices(indices);
  }

  @Override
  public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
    if (dirty) {
      int numVerts = calculateVertices(vertices);
      numVertices = numVerts / 4 * 6;
      mesh.setVertices(vertices, 0, numVerts * VERTEX_SIZE);
      dirty = false;
    }
    if (numVertices == 0) return;
    Renderable renderable = pool.obtain();
    renderable.material = GraphicsHelper.getBlockTextureSheet();
    renderable.mesh = mesh;
    renderable.meshPartOffset = 0;
    renderable.meshPartSize = numVertices;
    renderable.primitiveType = GL20.GL_TRIANGLES;
    renderables.add(renderable);
  }

  public int calculateVertices(float[] vertices) {
    Area maxX = ModularWorldClient.instance.world.getArea(area.x + 1, area.y, area.z);
    Area minX = ModularWorldClient.instance.world.getArea(area.x - 1, area.y, area.z);
    Area maxY = ModularWorldClient.instance.world.getArea(area.x, area.y + 1, area.z);
    Area minY = ModularWorldClient.instance.world.getArea(area.x, area.y - 1, area.z);
    Area maxZ = ModularWorldClient.instance.world.getArea(area.x, area.y, area.z + 1);
    Area minZ = ModularWorldClient.instance.world.getArea(area.x, area.y, area.z - 1);

    int i = 0;
    int vertexOffset = 0;
    for (int y = 0; y < SIZE_BLOCKS; y++) {
      for (int z = 0; z < SIZE_BLOCKS; z++) {
        for (int x = 0; x < SIZE_BLOCKS; x++, i++) {
          Block block = area.blocks[i];
          if (block == null) continue;
          BlockTextureHandler textureHandler = block.getTextureHandler();
          if (x < SIZE_BLOCKS - 1) {
            if (area.blocks[i + MAX_X_OFFSET] == null)
              vertexOffset = createMaxX(offset, textureHandler.getSide(Direction.posX).textureRegion, x, y, z, vertices, vertexOffset);
          } else if (maxX.getBlock(0, y, z) == null) {
            vertexOffset = createMaxX(offset, textureHandler.getSide(Direction.posX).textureRegion, x, y, z, vertices, vertexOffset);
          }
          if (x > 0) {
            if (area.blocks[i + MIN_X_OFFSET] == null)
              vertexOffset = createMinX(offset, textureHandler.getSide(Direction.negX).textureRegion, x, y, z, vertices, vertexOffset);
          } else if (minX.getBlock(31, y, z) == null) {
            vertexOffset = createMinX(offset, textureHandler.getSide(Direction.negX).textureRegion, x, y, z, vertices, vertexOffset);
          }
          if (y < SIZE_BLOCKS - 1) {
            if (area.blocks[i + MAX_Y_OFFSET] == null)
              vertexOffset = createMaxY(offset, textureHandler.getSide(Direction.posY).textureRegion, x, y, z, vertices, vertexOffset);
          } else if (maxY.getBlock(x, 0, z) == null) {
            vertexOffset = createMaxY(offset, textureHandler.getSide(Direction.posY).textureRegion, x, y, z, vertices, vertexOffset);
          }
          if (y > 0) {
            if (area.blocks[i + MIN_Y_OFFSET] == null)
              vertexOffset = createMinY(offset, textureHandler.getSide(Direction.negY).textureRegion, x, y, z, vertices, vertexOffset);
          } else if (minY.getBlock(x, 31, z) == null) {
            vertexOffset = createMinY(offset, textureHandler.getSide(Direction.negY).textureRegion, x, y, z, vertices, vertexOffset);
          }
          if (z < SIZE_BLOCKS - 1) {
            if (area.blocks[i + MAX_Z_OFFSET] == null)
              vertexOffset = createMaxZ(offset, textureHandler.getSide(Direction.posZ).textureRegion, x, y, z, vertices, vertexOffset);
          } else if (maxZ.getBlock(x, y, 0) == null) {
            vertexOffset = createMaxZ(offset, textureHandler.getSide(Direction.posZ).textureRegion, x, y, z, vertices, vertexOffset);
          }
          if (z > 0) {
            if (area.blocks[i + MIN_Z_OFFSET] == null)
              vertexOffset = createMinZ(offset, textureHandler.getSide(Direction.negZ).textureRegion, x, y, z, vertices, vertexOffset);
          } else if (minZ.getBlock(x, y, 31) == null) {
            vertexOffset = createMinZ(offset, textureHandler.getSide(Direction.negZ).textureRegion, x, y, z, vertices, vertexOffset);
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
}
