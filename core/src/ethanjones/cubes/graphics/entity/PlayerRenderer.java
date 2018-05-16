package ethanjones.cubes.graphics.entity;

import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.graphics.CubesRenderable;
import ethanjones.cubes.graphics.CubesVertexAttributes;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.world.block.FaceVertices;

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
  private static CubesRenderable renderable = new CubesRenderable();

  public static void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool, Player player) {
    if (mesh == null) {
      TextureRegion regionPlayer = Assets.getTextureRegion("core:world/player.png");

      mesh = new Mesh(false, 4 * 6, 6 * 6, CubesVertexAttributes.VERTEX_ATTRIBUTES);
      mesh.setIndices(blockIndices);
      int vertexOffset = 0;
      vertices = new float[CubesVertexAttributes.COMPONENTS * 4 * 6];
      Vector3 offset = new Vector3(-0.5f, -0.5f, -0.5f);
      vertexOffset = FaceVertices.createMaxX(offset, regionPlayer, null, 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
      vertexOffset = FaceVertices.createMaxY(offset, regionPlayer, null, 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
      vertexOffset = FaceVertices.createMaxZ(offset, regionPlayer, null, 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
      vertexOffset = FaceVertices.createMinX(offset, regionPlayer, null, 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
      vertexOffset = FaceVertices.createMinY(offset, regionPlayer, null, 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
      vertexOffset = FaceVertices.createMinZ(offset, regionPlayer, null, 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
      mesh.setVertices(vertices);

      renderable.meshPart.offset = 0;
      renderable.meshPart.size = 6 * 6;
      renderable.meshPart.mesh = mesh;
      renderable.material = Assets.getMaterial("core:world/player.png");
      renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
      renderable.name = "Player";
    }
    renderable.worldTransform.idt();
    renderable.worldTransform.translate(player.position.x, player.position.y, player.position.z);
    renderable.worldTransform.scl(0.5f);
    renderable.worldTransform.rotate(Vector3.Y, (System.currentTimeMillis() % 3600) / 10);
    renderable.setLightOverride(player.position);
    renderables.add(renderable);
  }
}
