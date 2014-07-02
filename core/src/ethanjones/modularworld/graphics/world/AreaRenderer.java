package ethanjones.modularworld.graphics.world;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.utils.Array;
import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.core.util.Direction;
import ethanjones.modularworld.graphics.GraphicsHelper;
import ethanjones.modularworld.world.World;
import ethanjones.modularworld.world.storage.Area;

import static ethanjones.modularworld.world.storage.Area.SIZE_BLOCKS;

public class AreaRenderer {

  public MeshBuilder meshBuilder = new MeshBuilder();
  public FaceProvider faceProvider;
  public Array<Mesh> meshes;
  private Array<Model> models;
  private Array<ModelInstance> modelInstances;
  private Camera camera;
  private Area area;
  private Array<Renderable> array;
  private boolean rebuild;

  public AreaRenderer(Area area) {
    this.area = area;
    faceProvider = new FaceProvider(area);
    modelInstances = new Array<ModelInstance>();
    models = new Array<Model>();
    meshes = new Array<Mesh>();
  }

  public static Model createFromMesh(final Mesh mesh) {
    Model result = new Model();
    MeshPart meshPart = new MeshPart();
    meshPart.id = "part1";
    meshPart.indexOffset = 0;
    meshPart.numVertices = mesh.getNumIndices();
    meshPart.primitiveType = GL20.GL_POINTS;
    meshPart.mesh = mesh;

    NodePart partMaterial = new NodePart();
    partMaterial.material = GraphicsHelper.blockPackedTextures;
    partMaterial.meshPart = meshPart;
    Node node = new Node();
    node.id = "node1";
    node.parts.add(partMaterial);

    result.meshes.add(mesh);
    result.materials.add(GraphicsHelper.blockPackedTextures);
    result.nodes.add(node);
    result.meshParts.add(meshPart);
    result.manageDisposable(mesh);
    return result;
  }

  public AreaRenderer setCamera(Camera camera) {
    this.camera = camera;
    return this;
  }

  /**
   * public void rebuildArray() {
   * this.rebuildArray = true;
   * }
   * <p/>
   * private void rebuild() {
   * array.clear();
   * for (int rX = 0; rX < SIZE_RENDER_AREA; rX++) {
   * for (int rY = 0; rY < SIZE_RENDER_AREA; rY++) {
   * for (int rZ = 0; rZ < SIZE_RENDER_AREA; rZ++) {
   * array.addAll(renderAreas[rX][rY][rZ].data);
   * }
   * }
   * }
   * }
   */

  public Array<ModelInstance> get() {
    if (area.modified) {
      area.modified = false;
      for (Model model : models) {
        model.dispose();
      }
      meshes.clear();
      models.clear();
      modelInstances.clear();
      World world = ModularWorld.instance.world;
      meshBuilder.begin(GraphicsHelper.usage);
      for (int x = 0; x < SIZE_BLOCKS; x++) {
        for (int y = 0; y < SIZE_BLOCKS; y++) {
          for (int z = 0; z < SIZE_BLOCKS; z++) {
            if (!faceProvider.set(x, y, z, world)) continue;
            if (world.getBlock(faceProvider.x + 1, faceProvider.y, faceProvider.z) == null) {
              faceProvider.addTo(meshBuilder, Direction.posX);
            }
            if (world.getBlock(faceProvider.x - 1, faceProvider.y, faceProvider.z) == null) {
              faceProvider.addTo(meshBuilder, Direction.negX);
            }

            if (faceProvider.y != World.HEIGHT_LIMIT && world.getBlock(faceProvider.x, faceProvider.y + 1, faceProvider.z) == null) {
              faceProvider.addTo(meshBuilder, Direction.posY);
            }
            if (faceProvider.y != 0 && world.getBlock(faceProvider.x, faceProvider.y - 1, faceProvider.z) == null) {
              faceProvider.addTo(meshBuilder, Direction.negY);
            }

            if (world.getBlock(faceProvider.x, faceProvider.y, faceProvider.z + 1) == null) {
              faceProvider.addTo(meshBuilder, Direction.posZ);
            }
            if (world.getBlock(faceProvider.x, faceProvider.y, faceProvider.z - 1) == null) {
              faceProvider.addTo(meshBuilder, Direction.negZ);
            }
            if (faceProvider.v > 8000) {
              faceProvider.v = 0;
              meshes.add(meshBuilder.end());
              meshBuilder.begin(GraphicsHelper.usage);
            }
          }
        }
      }
      meshes.add(meshBuilder.end());
      for (Mesh mesh : meshes) {
        Model model = createFromMesh(mesh);
        models.add(model);
        modelInstances.add(new ModelInstance(model));
      }
    }
    return modelInstances;
  }
}
