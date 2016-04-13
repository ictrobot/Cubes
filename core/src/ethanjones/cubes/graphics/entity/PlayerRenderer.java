package ethanjones.cubes.graphics.entity;

import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.world.AreaMesh;
import ethanjones.cubes.graphics.world.FaceVertices;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.light.LightNode;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import static ethanjones.cubes.world.light.BlockLight.FULL_LIGHT;

public class PlayerRenderer {
  static short[] blockIndices;

  static {
    blockIndices = new short[6 * 6];
    short j = 0;
    for (int i = 0; i < blockIndices.length; i += 6, j += 4) {
      blockIndices[i + 0] = (short) (j + 0);
      blockIndices[i + 1] = (short) (j + 1);
      blockIndices[i + 2] = (short) (j + 2);
      blockIndices[i + 3] = (short) (j + 2);
      blockIndices[i + 4] = (short) (j + 3);
      blockIndices[i + 5] = (short) (j + 0);
    }
  }

  static Mesh mesh;
  static float[] vertices;

  public static void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool, Player player) {
    if (mesh == null) {
      TextureRegion regionPlayer = Assets.getPackedTexture("core:block/player.png");
      TextureRegion regionFacing = Assets.getPackedTexture("core:block/player_facing.png");

      mesh = new Mesh(false, 4 * 6, 6 * 6, AreaMesh.vertexAttributes);
      mesh.setIndices(blockIndices);
      int vertexOffset = 0;
      vertices = new float[AreaMesh.VERTEX_SIZE * 4 * 6];
      Vector3 offset = new Vector3(-0.5f, 0f, -0.5f);
      vertexOffset = FaceVertices.createMaxX(offset, regionFacing, 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
      vertexOffset = FaceVertices.createMaxY(offset, regionPlayer, 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
      vertexOffset = FaceVertices.createMaxZ(offset, regionPlayer, 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
      vertexOffset = FaceVertices.createMinX(offset, regionPlayer, 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
      vertexOffset = FaceVertices.createMinY(offset, regionPlayer, 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
      vertexOffset = FaceVertices.createMinZ(offset, regionPlayer, 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
      mesh.setVertices(vertices);
    }
    Renderable renderable = new Renderable();
    renderable.worldTransform.translate(player.position.x, player.position.y - 0.25f, player.position.z);
    renderable.worldTransform.scl(0.5f);
    renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
    renderable.meshPart.offset = 0;
    renderable.meshPart.size = 6 * 6;
    renderable.meshPart.mesh = mesh;
    renderable.material = Assets.packedTextureSheet.getMaterial();

    LightNode lightNode = new LightNode(CoordinateConverter.block(player.position.x), CoordinateConverter.block(player.position.y), CoordinateConverter.block(player.position.z), 0);
    lightNode.l = Cubes.getClient().world.getLightRaw(lightNode.x, lightNode.y, lightNode.z);

    renderable.userData = lightNode;
    renderables.add(renderable);
  }
}
