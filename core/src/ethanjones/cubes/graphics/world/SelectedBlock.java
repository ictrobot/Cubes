package ethanjones.cubes.graphics.world;

import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.collision.BlockIntersection;
import ethanjones.cubes.world.reference.BlockReference;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.math.Vector3;

import static ethanjones.cubes.world.light.BlockLight.FULL_LIGHT;

public class SelectedBlock {
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
  static TextureRegion textureRegion;
  static Material material;

  public static Renderable draw() {
    if (Cubes.getClient().renderer.guiRenderer.isHideGuiEnabled()) return null;
    BlockIntersection blockIntersection = BlockIntersection.getBlockIntersection(Cubes.getClient().player.position, Cubes.getClient().player.angle, Cubes.getClient().world);
    if (blockIntersection == null || blockIntersection.getBlockReference() == null) return null;
    BlockReference position = blockIntersection.getBlockReference();

    if (mesh == null) {
      textureRegion = Assets.getTextureRegion("core:hud/SelectedBlock.png");
      material = Assets.getMaterial("core:hud/SelectedBlock.png");
      material.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));

      mesh = new Mesh(false, 4 * 6, 6 * 6, AreaMesh.vertexAttributes);
      mesh.setIndices(blockIndices);
      int vertexOffset = 0;
      vertices = new float[AreaMesh.VERTEX_SIZE * 4 * 6];
      vertexOffset = FaceVertices.createMaxX(Vector3.Zero, textureRegion, 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
      vertexOffset = FaceVertices.createMaxY(Vector3.Zero, textureRegion, 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
      vertexOffset = FaceVertices.createMaxZ(Vector3.Zero, textureRegion, 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
      vertexOffset = FaceVertices.createMinX(Vector3.Zero, textureRegion, 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
      vertexOffset = FaceVertices.createMinY(Vector3.Zero, textureRegion, 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
      vertexOffset = FaceVertices.createMinZ(Vector3.Zero, textureRegion, 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
      mesh.setVertices(vertices);
    }
    float f = 1f / 64f;
    Renderable renderable = new Renderable();
    renderable.worldTransform.translate(position.blockX - f, position.blockY - f, position.blockZ - f);
    renderable.worldTransform.scl(1f + f + f);
    renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
    renderable.meshPart.offset = 0;
    renderable.meshPart.size = 6 * 6;
    renderable.meshPart.mesh = mesh;
    renderable.material = material;
    return renderable;
  }
}
