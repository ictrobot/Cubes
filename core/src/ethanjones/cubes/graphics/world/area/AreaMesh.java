package ethanjones.cubes.graphics.world.area;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import ethanjones.cubes.graphics.CubesRenderable;
import ethanjones.cubes.graphics.CubesVertexAttributes;
import ethanjones.cubes.graphics.assets.Assets;

public class AreaMesh implements Pool.Poolable, Disposable {

  public static final int MAX_INDICES = 32760;
  public static final int MAX_SIDES = MAX_INDICES / 6;
  public static final int MAX_VERTICES = MAX_SIDES * 4;

  public static short[] indices;
  public static float[] vertices;

  static {
    vertices = new float[MAX_VERTICES * CubesVertexAttributes.MAX_COMPONENTS * 2];
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

  public final CubesRenderable renderable = new CubesRenderable();
  public Mesh mesh;
  public MeshPart meshPart;
  public int vertexCount;
  public int maxVertexOffset = 0;

  public AreaMesh() {
    this(CubesVertexAttributes.getVertexAttributes());
  }

  public AreaMesh(VertexAttributes vertexAttributes) {
    mesh = new Mesh(true, MAX_VERTICES, MAX_INDICES, vertexAttributes);
    meshPart = new MeshPart();
    meshPart.mesh = mesh;
    meshPart.primitiveType = GL20.GL_TRIANGLES;
    meshPart.offset = 0;
    mesh.setIndices(indices);

    int components = CubesVertexAttributes.components(vertexAttributes);
    maxVertexOffset = MAX_VERTICES * components;

    renderable.material = Assets.blockItemSheet.getMaterial();
    renderable.name = "AreaMesh";
  }

  public void saveVertices(int vertexCount) {
    mesh.setVertices(vertices, 0, vertexCount);
    int v = vertexCount / CubesVertexAttributes.components(mesh.getVertexAttributes());
    meshPart.size = v / 4 * 6;
    this.vertexCount = vertexCount;
    if (vertexCount > 0) {
      meshPart.update();
      renderable.meshPart.set(meshPart);
    }
  }

  @Override
  public void reset() {
    renderable.name = "AreaMesh";
  }

  @Override
  public void dispose() {
    mesh.dispose();
    renderable.name = "Disposed AreaMesh";
  }
}
