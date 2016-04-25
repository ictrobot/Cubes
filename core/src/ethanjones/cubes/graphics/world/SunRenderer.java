package ethanjones.cubes.graphics.world;

import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.collision.BlockIntersection;
import ethanjones.cubes.world.reference.BlockReference;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import static ethanjones.cubes.world.light.BlockLight.FULL_LIGHT;

public class SunRenderer {
  static short[] indicies;

  static {
    indicies = new short[6];
    short j = 0;
    for (int i = 0; i < indicies.length; i += 6, j += 4) {
      indicies[i + 0] = (short) (j + 0);
      indicies[i + 1] = (short) (j + 1);
      indicies[i + 2] = (short) (j + 2);
      indicies[i + 3] = (short) (j + 2);
      indicies[i + 4] = (short) (j + 3);
      indicies[i + 5] = (short) (j + 0);
    }
  }

  static Mesh mesh;
  static float[] vertices;
  static TextureRegion textureRegion;
  static Material material;

  public static Renderable draw() {
    if (mesh == null) {
      textureRegion = Assets.getTextureRegion("core:hud/Sun.png");
      material = Assets.getMaterial("core:hud/Sun.png");
      material.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));

      mesh = new Mesh(false, 4, 6, AreaMesh.vertexAttributes);
      mesh.setIndices(indicies);
      vertices = new float[AreaMesh.VERTEX_SIZE * 4];
      FaceVertices.createMinY(new Vector3(-0.5f, 0f, -0.5f), textureRegion, 0, 0, 0, FULL_LIGHT, vertices, 0);

      mesh.setVertices(vertices);
    }
    Renderable renderable = new Renderable();
    doStuff(renderable);

    renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
    renderable.meshPart.offset = 0;
    renderable.meshPart.size = 6;
    renderable.meshPart.mesh = mesh;
    renderable.material = material;
    return renderable;
  }

  public static void doStuff(Renderable renderable) {
    Vector3 pos = Cubes.getClient().player.position;
    int r = 512;
    float f = (float) (Cubes.getClient().world.time - (World.MAX_TIME / 4)) / (float) World.MAX_TIME;
    f %= 1;

    float x = (float) (pos.x + (r * Math.cos(f * 2 * Math.PI)));
    float y = (float) (pos.y + (r * Math.sin(f * 2 * Math.PI)));
    System.out.println(f + " " + x + " " + y);
    float z = pos.z;

    renderable.worldTransform.setToTranslation(x, y, z);
    renderable.worldTransform.scl(75f);
    renderable.worldTransform.rotate(Vector3.Z, (f - 0.25f % 1) * 360);
  }
}
