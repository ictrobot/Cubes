package ethanjones.cubes.graphics.world;

import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.item.ItemTool;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.reference.BlockReference;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;

import static ethanjones.cubes.world.light.BlockLight.FULL_LIGHT;

public class BreakingRenderer {

  static Mesh mesh;
  static Material material;
  static int num;
  static int size;

  static {
    Texture texture = Assets.getTexture("core:world/breaking.png");
    material = new Material(TextureAttribute.createDiffuse(texture));
    material.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));

    if (texture.getHeight() % texture.getWidth() != 0) throw new CubesException("Invalid breaking texture");
    size = texture.getWidth();
    num = texture.getHeight() / size;
    mesh = new Mesh(false, 4 * 6 * num, 6 * 6 * num, AreaMesh.vertexAttributes);

    short[] indicies = new short[6 * 6 * num];
    short j = 0;
    for (int i = 0; i < indicies.length; i += 6, j += 4) {
      indicies[i + 0] = (short) (j + 0);
      indicies[i + 1] = (short) (j + 1);
      indicies[i + 2] = (short) (j + 2);
      indicies[i + 3] = (short) (j + 2);
      indicies[i + 4] = (short) (j + 3);
      indicies[i + 5] = (short) (j + 0);
    }
    mesh.setIndices(indicies);

    float[] vertices = new float[AreaMesh.VERTEX_SIZE * 4 * 6 * num];
    int vertexOffset = 0;
    for (int i = 0; i < num; i++) {
      TextureRegion textureRegion = new TextureRegion(texture, 0, i * size, size, size);
      vertexOffset = FaceVertices.createMaxX(Vector3.Zero, textureRegion, 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
      vertexOffset = FaceVertices.createMaxY(Vector3.Zero, textureRegion, 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
      vertexOffset = FaceVertices.createMaxZ(Vector3.Zero, textureRegion, 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
      vertexOffset = FaceVertices.createMinX(Vector3.Zero, textureRegion, 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
      vertexOffset = FaceVertices.createMinY(Vector3.Zero, textureRegion, 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
      vertexOffset = FaceVertices.createMinZ(Vector3.Zero, textureRegion, 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
    }
    mesh.setVertices(vertices);
  }

  public static Renderable draw() {
    Player player = Cubes.getClient().player;
    ItemTool.MiningTarget currentlyMining = player.getCurrentlyMining();
    if (currentlyMining == null) return null;
    BlockReference position = currentlyMining.target;
    float percent = currentlyMining.time / currentlyMining.totalTime;
    if (percent <= 1f / (1f + num)) return null;
    int n = (int) Math.floor(percent * (1f + num));

    float f = 1f / 128f;
    Renderable renderable = new Renderable();
    renderable.worldTransform.translate(position.blockX - f, position.blockY - f, position.blockZ - f);
    renderable.worldTransform.scl(1f + f + f);
    renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
    renderable.meshPart.offset = n * (6 * 6);
    renderable.meshPart.size = 6 * 6;
    renderable.meshPart.mesh = mesh;
    renderable.material = material;
    return renderable;
  }
}
