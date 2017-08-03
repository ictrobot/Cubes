package ethanjones.cubes.graphics.world;

import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.World;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
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
  static Material sunMaterial;
  static Material moonMaterial;

  public static void draw(ModelBatch modelBatch) {
    if (mesh == null) {
      textureRegion = Assets.getTextureRegion("core:world/sun.png");
      sunMaterial = Assets.getMaterial("core:world/sun.png");
      sunMaterial.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));

      moonMaterial = Assets.getMaterial("core:world/moon.png");
      moonMaterial.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));

      mesh = new Mesh(false, 4, 6, CubesVertexAttributes.VERTEX_ATTRIBUTES);
      mesh.setIndices(indicies);
      vertices = new float[CubesVertexAttributes.COMPONENTS * 4];
      FaceVertices.createMinY(new Vector3(-0.5f, 0f, -0.5f), textureRegion, null, 0, 0, 0, FULL_LIGHT, vertices, 0);

      mesh.setVertices(vertices);
    }
    Renderable sun = new Renderable();
    setWorldTransform(sun, false);
    sun.meshPart.primitiveType = GL20.GL_TRIANGLES;
    sun.meshPart.offset = 0;
    sun.meshPart.size = 6;
    sun.meshPart.mesh = mesh;
    sun.material = sunMaterial;
    sun.userData = new RenderingSettings().setFogEnabled(false);
    modelBatch.render(sun);

    Renderable moon = new Renderable().set(sun);
    setWorldTransform(moon, true);
    moon.material = moonMaterial;
    modelBatch.render(moon);
  }

  public static void setWorldTransform(Renderable renderable, boolean isMoon) {
    Vector3 pos = Cubes.getClient().player.position;
    int r = 512;
    float f = (float) (Cubes.getClient().world.getTime() - (World.MAX_TIME / 4)) / (float) World.MAX_TIME;
    if (isMoon) f += 0.5f;
    f %= 1;

    float x = (float) (pos.x + (r * Math.cos(f * 2 * Math.PI)));
    float y = (float) (pos.y + (r * Math.sin(f * 2 * Math.PI)));
    float z = pos.z;

    renderable.worldTransform.setToTranslation(x, y, z);
    renderable.worldTransform.scl(75f);
    renderable.worldTransform.rotate(Vector3.Z, (f - 0.25f % 1) * 360);
  }
}
