package ethanjones.cubes.entity;

import ethanjones.cubes.core.util.BlockFace;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.world.AreaMesh;
import ethanjones.cubes.graphics.world.BlockTextureHandler;
import ethanjones.cubes.graphics.world.FaceVertices;
import ethanjones.cubes.side.Sided;
import ethanjones.data.DataGroup;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class ItemEntity extends Entity implements RenderableProvider {
  static short[] indices;

  static {
    indices = new short[6 * 6];
    short j = 0;
    for (int i = 0; i < indices.length; i += 6, j += 4) {
      indices[i + 0] = (short) (j + 0);
      indices[i + 1] = (short) (j + 1);
      indices[i + 2] = (short) (j + 2);
      indices[i + 3] = (short) (j + 2);
      indices[i + 4] = (short) (j + 3);
      indices[i + 5] = (short) (j + 0);
    }
  }

  public int block;

  Mesh mesh;
  float[] vertices;

  public ItemEntity() {
    super("core:item");
  }

  @Override
  public DataGroup write() {
    DataGroup write = super.write();
    write.put("block", block);
    return write;
  }

  @Override
  public void read(DataGroup data) {
    super.read(data);
    block = data.getInteger("block");
  }

  @Override
  public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
    if (mesh == null) {
      mesh = new Mesh(false, 4 * 6, 6 * 6, AreaMesh.vertexAttributes);
      mesh.setIndices(indices);
      int vertexOffset = 0;
      vertices = new float[AreaMesh.VERTEX_SIZE * 4 * 6];
      BlockTextureHandler textureHandler = Sided.getIDManager().toBlock(block).getTextureHandler();
      Vector3 offset = new Vector3(-0.5f, 0f, -0.5f);
      vertexOffset = FaceVertices.createMaxX(offset, textureHandler.getSide(BlockFace.posX), 0, 0, 0, vertices, vertexOffset);
      vertexOffset = FaceVertices.createMaxY(offset, textureHandler.getSide(BlockFace.posY), 0, 0, 0, vertices, vertexOffset);
      vertexOffset = FaceVertices.createMaxZ(offset, textureHandler.getSide(BlockFace.posZ), 0, 0, 0, vertices, vertexOffset);
      vertexOffset = FaceVertices.createMinX(offset, textureHandler.getSide(BlockFace.negX), 0, 0, 0, vertices, vertexOffset);
      vertexOffset = FaceVertices.createMinY(offset, textureHandler.getSide(BlockFace.negY), 0, 0, 0, vertices, vertexOffset);
      vertexOffset = FaceVertices.createMinZ(offset, textureHandler.getSide(BlockFace.negZ), 0, 0, 0, vertices, vertexOffset);
      mesh.setVertices(vertices);
    }
    Renderable renderable = new Renderable();
    renderable.worldTransform.translate(position.x, position.y + yOffset(), position.z);
    renderable.worldTransform.scl(0.3f);
    renderable.worldTransform.rotate(Vector3.Y, (System.currentTimeMillis() % 7200) / 20);
    renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
    renderable.meshPart.offset = 0;
    renderable.meshPart.size = 6 * 6;
    renderable.meshPart.mesh = mesh;
    renderable.material = Assets.packedTextureSheet.getMaterial();
    renderables.add(renderable);
  }

  private static float yOffset() {
    long l = System.currentTimeMillis();
    l %= 2000;
    if (l > 1000) l = 2000 - l;
    float f = (float) l / 8000f;
    return f;
  }

  @Override
  public void dispose() {
    super.dispose();
    if (mesh != null) mesh.dispose();
  }
}
