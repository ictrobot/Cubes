package ethanjones.cubes.graphics.world.other;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.graphics.CubesRenderable;
import ethanjones.cubes.graphics.CubesVertexAttributes;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.world.block.FaceVertices;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.storage.Area;

import static ethanjones.cubes.world.light.BlockLight.FULL_LIGHT;

public class SunRenderer {

  private static String sunTexturePath = "core:world/sun.png";
  private static String moonTexturePath = "core:world/moon.png";

  private boolean moon;
  private Mesh mesh;
  private float[] vertices;
  private short[] indices;
  private Material material;
  private TextureRegion textureRegion;
  private CubesRenderable renderable;

  private SunRenderer(boolean moon) {
    this.moon = moon;

    String texturePath = moon ? moonTexturePath : sunTexturePath;
    material = Assets.getMaterial(texturePath);
    textureRegion = Assets.getTextureRegion(texturePath);

    indices = new short[6];
    short j = 0;
    for (int i = 0; i < indices.length; i += 6, j += 4) {
      indices[i + 0] = (short) (j + 0);
      indices[i + 1] = (short) (j + 1);
      indices[i + 2] = (short) (j + 2);
      indices[i + 3] = (short) (j + 2);
      indices[i + 4] = (short) (j + 3);
      indices[i + 5] = (short) (j + 0);
    }
    vertices = new float[CubesVertexAttributes.COMPONENTS * 4];

    mesh = new Mesh(false, 4, 6, CubesVertexAttributes.VERTEX_ATTRIBUTES);
    mesh.setIndices(indices);
    FaceVertices.createMinY(new Vector3(-0.5f, 0f, -0.5f), textureRegion, null, 0, 0, 0, FULL_LIGHT, vertices, 0);
    mesh.setVertices(vertices);

    renderable = new CubesRenderable();
    renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
    renderable.meshPart.offset = 0;
    renderable.meshPart.size = 6;
    renderable.meshPart.mesh = mesh;
    renderable.material = material;
    renderable.setFogEnabled(false);
  }

  private void setWorldTransform() {
    Vector3 pos = Cubes.getClient().player.position;

    int renderDistance = Settings.getIntegerSettingValue(Settings.GRAPHICS_VIEW_DISTANCE);
    float radius = Math.max(256, renderDistance * Area.SIZE_BLOCKS * 1.1f);
    float scale = radius / 512f * 75f;

    float f = (float) (Cubes.getClient().world.getTime() - (World.MAX_TIME / 4)) / (float) World.MAX_TIME;
    if (moon) f += 0.5f;
    f %= 1;

    float x = (float) (pos.x + (radius * Math.cos(f * 2 * Math.PI)));
    float y = (float) (pos.y + (radius * Math.sin(f * 2 * Math.PI)));
    float z = pos.z;

    renderable.worldTransform.idt();
    renderable.worldTransform.translate(x, y, z);
    renderable.worldTransform.scl(scale);
    renderable.worldTransform.rotate(Vector3.Z, (f - 0.25f % 1) * 360);
  }

  private void render(ModelBatch modelBatch) {
    setWorldTransform();
    modelBatch.render(renderable);
  }

  private static SunRenderer SUN;
  private static SunRenderer MOON;

  public static void draw(ModelBatch modelBatch) {
    if (SUN == null) SUN = new SunRenderer(false);
    SUN.render(modelBatch);

    if (MOON == null) MOON = new SunRenderer(true);
    MOON.render(modelBatch);
  }
}
