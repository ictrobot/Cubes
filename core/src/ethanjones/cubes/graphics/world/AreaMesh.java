package ethanjones.cubes.graphics.world;

import ethanjones.cubes.graphics.assets.Assets;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Pool;

public class AreaMesh implements Pool.Poolable {
  public static final VertexAttribute positionAttribute = new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE);
  public static final VertexAttribute textureAttribute = new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0");
  public static final VertexAttribute lightAttribute = new VertexAttribute(VertexAttributes.Usage.Generic, 1, "a_voxellight");
  public static final VertexAttributes vertexAttributes = new VertexAttributes(positionAttribute, textureAttribute, lightAttribute);
  public static final int VERTEX_SIZE = 6; //3 for position, 2 for texture coordinates 1 for light

  public static final int MAX_VERTICES = 65532;
  public static final int SAFE_VERTICES = MAX_VERTICES - (6 * 4 * VERTEX_SIZE);
  public static final int MAX_INDICES = MAX_VERTICES / 4 * 6;
  
  public static short[] indices;
  public static float[] vertices;

  static {
    vertices = new float[MAX_VERTICES];
    indices = new short[MAX_INDICES];
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

  public Mesh mesh;
  public MeshPart meshPart;
  public int vertexCount;

  public AreaMesh() {
    mesh = new Mesh(true, MAX_VERTICES, MAX_INDICES, vertexAttributes);
    meshPart = new MeshPart();
    meshPart.mesh = mesh;
    meshPart.primitiveType = GL20.GL_TRIANGLES;
    meshPart.offset = 0;
    mesh.setIndices(indices);
  }

  public void saveVertices(int vertexCount) {
    mesh.setVertices(vertices, 0, vertexCount);
    int v = vertexCount / VERTEX_SIZE;
    meshPart.size = v / 4 * 6;
    this.vertexCount = vertexCount;
    if (vertexCount > 0) meshPart.update();
  }

  public Renderable renderable(Pool<Renderable> pool) {
    Renderable renderable = pool.obtain();
    renderable.material = Assets.packedTextureSheet.getMaterial();
    renderable.meshPart.set(meshPart);
    return renderable;
  }

  @Override
  public void reset() {

  }
}
