package ethanjones.modularworld.graphics.world;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import ethanjones.modularworld.block.Block;
import ethanjones.modularworld.core.util.Direction;
import ethanjones.modularworld.graphics.GraphicsHelper;
import ethanjones.modularworld.graphics.PackedTexture;
import ethanjones.modularworld.world.storage.Area;

import static ethanjones.modularworld.world.storage.Area.SIZE_BLOCKS;

public class AreaRenderer {

  private static TextureRegion[] regions = new TextureRegion[1024];
  public MeshBuilder meshBuilder = new MeshBuilder();
  public FaceProvider faceProvider;
  public Array<Mesh> meshes;
  Vector3 u = new Vector3();
  Vector3 v = new Vector3();
  Vector3 normal = new Vector3();
  int mul = 0;
  private Array<Model> models;
  private Array<ModelInstance> modelInstances;
  private Camera camera;
  private Area area;

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
    meshPart.primitiveType = GL20.GL_TRIANGLES;
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
      greedy();
      /**
       for (Model model : models) {
       model.dispose();
       }
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
       **/
      for (Mesh mesh : meshes) {
        Model model = createFromMesh(mesh);
        models.add(model);
        modelInstances.add(new ModelInstance(model));
      }
    }
    return modelInstances;
  }

  /**
   * https://github.com/roboleary/GreedyMesh/blob/master/src/mygame/Main.java
   */

  void greedy() {

    final int NORTH = Direction.posX.ordinal();
    final int EAST = Direction.posZ.ordinal();
    final int SOUTH = Direction.negX.ordinal();
    final int WEST = Direction.negZ.ordinal();
    final int TOP = Direction.posY.ordinal();
    final int BOTTOM = Direction.negY.ordinal();

    int i, j, k, l, w, h, u, v, n, side = 0;

    final int[] x = new int[]{0, 0, 0};
    final int[] q = new int[]{0, 0, 0};
    final int[] du = new int[]{0, 0, 0};
    final int[] dv = new int[]{0, 0, 0};

        /*
         * We create a mask - this will contain the groups of matching voxel faces
         * as we proceed through the chunk in 6 directions - once for each face.
         */
    final VoxelFace[] mask = new VoxelFace[SIZE_BLOCKS * SIZE_BLOCKS];

        /*
         * These are just working variables to hold two faces during comparison.
         */
    VoxelFace voxelFace, voxelFace1;

    /**
     * We start with the lesser-spotted boolean for-loop (also known as the old flippy floppy).
     *
     * The variable backFace will be TRUE on the first iteration and FALSE on the second - this allows
     * us to track which direction the indices should run during creation of the quad.
     *
     * This loop runs twice, and the inner loop 3 times - totally 6 iterations - one for each
     * voxel face.
     */
    for (boolean backFace = true, b = false; b != backFace; backFace = backFace && b, b = !b) {

            /*
             * We sweep over the 3 dimensions - most of what follows is well described by Mikola Lysenko
             * in his post - and is ported from his Javascript implementation.  Where this implementation
             * diverges, I've added commentary.
             */
      for (int d = 0; d < 3; d++) {

        u = (d + 1) % 3;
        v = (d + 2) % 3;

        x[0] = 0;
        x[1] = 0;
        x[2] = 0;

        q[0] = 0;
        q[1] = 0;
        q[2] = 0;
        q[d] = 1;

                /*
                 * Here we're keeping track of the side that we're meshing.
                 */
        if (d == 0) {
          side = backFace ? WEST : EAST;
        } else if (d == 1) {
          side = backFace ? BOTTOM : TOP;
        } else if (d == 2) {
          side = backFace ? SOUTH : NORTH;
        }

                /*
                 * We move through the dimension from front to back
                 */
        for (x[d] = -1; x[d] < SIZE_BLOCKS; ) {

                    /*
                     * -------------------------------------------------------------------
                     *   We compute the mask
                     * -------------------------------------------------------------------
                     */
          n = 0;

          for (x[v] = 0; x[v] < SIZE_BLOCKS; x[v]++) {

            for (x[u] = 0; x[u] < SIZE_BLOCKS; x[u]++) {

                            /*
                             * Here we retrieve two voxel faces for comparison.
                             */
              voxelFace = (x[d] >= 0) ? getVoxelFace(x[0], x[1], x[2], side) : null;
              voxelFace1 = (x[d] < SIZE_BLOCKS - 1) ? getVoxelFace(x[0] + q[0], x[1] + q[1], x[2] + q[2], side) : null;

                            /*
                             * Note that we're using the equals function in the voxel face class here, which lets the faces
                             * be compared based on any number of attributes.
                             *
                             * Also, we choose the face to add to the mask depending on whether we're moving through on a backface or not.
                             */
              mask[n++] = ((voxelFace != null && voxelFace1 != null && voxelFace.equals(voxelFace1)))
                ? null
                : backFace ? voxelFace1 : voxelFace;
            }
          }

          x[d]++;

                    /*
                     * Now we generate the mesh for the mask
                     */
          n = 0;

          for (j = 0; j < SIZE_BLOCKS; j++) {

            for (i = 0; i < SIZE_BLOCKS; ) {

              if (mask[n] != null) {

                                /*
                                 * We compute the width
                                 */
                for (w = 1; i + w < SIZE_BLOCKS && mask[n + w] != null && mask[n + w].equals(mask[n]); w++) {
                }

                                /*
                                 * Then we compute height
                                 */
                boolean done = false;

                for (h = 1; j + h < SIZE_BLOCKS; h++) {

                  for (k = 0; k < w; k++) {

                    if (mask[n + k + h * SIZE_BLOCKS] == null || !mask[n + k + h * SIZE_BLOCKS].equals(mask[n])) {
                      done = true;
                      break;
                    }
                  }

                  if (done) {
                    break;
                  }
                }

                                /*
                                 * Here we check the "transparent" attribute in the VoxelFace class to ensure that we don't mesh
                                 * any culled faces.
                                 */
                if (!mask[n].transparent) {
                                    /*
                                     * Add quad
                                     */
                  x[u] = i;
                  x[v] = j;

                  du[0] = 0;
                  du[1] = 0;
                  du[2] = 0;
                  du[u] = w;

                  dv[0] = 0;
                  dv[1] = 0;
                  dv[2] = 0;
                  dv[v] = h;

                                    /*
                                     * And here we call the quad function in order to render a merged quad in the scene.
                                     *
                                     * We pass mask[n] to the function, which is an instance of the VoxelFace class containing
                                     * all the attributes of the face - which allows for variables to be passed to shaders - for
                                     * example lighting values used to create ambient occlusion.
                                     */
                  quad(x[0], x[1], x[2],
                    x[0] + du[0], x[1] + du[1], x[2] + du[2],
                    x[0] + du[0] + dv[0], x[1] + du[1] + dv[1], x[2] + du[2] + dv[2],
                    x[0] + dv[0], x[1] + dv[1], x[2] + dv[2],
                    w,
                    h,
                    mask[n],
                    backFace);
                }

                                /*
                                 * We zero out the mask
                                 */
                for (l = 0; l < h; ++l) {

                  for (k = 0; k < w; ++k) {
                    mask[n + k + l * SIZE_BLOCKS] = null;
                  }
                }

                                /*
                                 * And then finally increment the counters and continue
                                 */
                i += w;
                n += w;

              } else {

                i++;
                n++;
              }
            }
          }
        }
      }
    }
  }

  private VoxelFace getVoxelFace(final int x, final int y, final int z, final int side) {

    Block block = area.getBlock(x, y, z);
    if (block == null) return null;

    VoxelFace voxelFace = new VoxelFace();

    PackedTexture packedTexture = block.getTextureHandler().getSide(side);
    voxelFace.type = packedTexture.number;
    regions[voxelFace.type] = packedTexture.textureRegion;

    voxelFace.side = side;

    return voxelFace;
  }

  /**
   * This function renders a single quad in the scene. This quad may represent many adjacent voxel
   * faces - so in order to create the illusion of many faces, you might consider using a tiling
   * function in your voxel shader. For this reason I've included the quad width and height as parameters.
   * <p/>
   * For example, if your texture coordinates for a single voxel face were 0 - 1 on a given axis, they should now
   * be 0 - width or 0 - height. Then you can calculate the correct texture coordinate in your fragement
   * shader using coord.xy = fract(coord.xy).
   */
  void quad(float x00, float y00, float z00, float x10, float y10, float z10, float x11, float y11, float z11,
            float x01, float y01, float z01, final int width, final int height, final VoxelFace voxel, final boolean backFace) {

    if (backFace) {
      mul = 1;
    } else {
      mul = 1;
    }
    u.set(x10, y10, z10).sub(x00, y00, z00);
    v.set(x11, y11, z11).sub(x00, y00, z00);
    normal.x = mul * ((u.y * v.z) - (u.z * v.y));
    normal.y = mul * ((u.z * v.x) - (u.x * v.z));
    normal.z = mul * ((u.x * v.y) - (u.y * v.x));
    normal.nor();

    x00 += area.minBlockX;
    x10 += area.minBlockX;
    x11 += area.minBlockX;
    x01 += area.minBlockX;

    y00 += area.minBlockY;
    y10 += area.minBlockY;
    y11 += area.minBlockY;
    y01 += area.minBlockY;

    z00 += area.minBlockZ;
    z10 += area.minBlockZ;
    z11 += area.minBlockZ;
    z01 += area.minBlockZ;

    meshBuilder.begin(GraphicsHelper.usage, GL20.GL_TRIANGLES);
    meshBuilder.setUVRange(regions[voxel.type]);
    if (backFace) {
      meshBuilder.rect(x01, y01, z01, x11, y11, z11, x10, y10, z10, x00, y00, z00, normal.x, normal.y, normal.z);
    } else {
      meshBuilder.rect(x00, y00, z00, x10, y10, z10, x11, y11, z11, x01, y01, z01, normal.x, normal.y, normal.z);
    }
    meshes.add(meshBuilder.end());
  }

  class VoxelFace {

    public boolean transparent;
    public int type;
    public int side;

    public boolean equals(final VoxelFace face) {
      return face.transparent == this.transparent && face.type == this.type;
    }
  }
}
