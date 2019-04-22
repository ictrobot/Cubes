package ethanjones.cubes.graphics.world.area;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import ethanjones.cubes.core.settings.Keybinds;
import ethanjones.cubes.graphics.CubesRenderable;
import ethanjones.cubes.graphics.CubesVertexAttributes;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.world.light.BlockLight;
import ethanjones.cubes.world.storage.Area;

public class AreaBoundaries {

  private static int state = 0; // 0 = disabled, 1 = area, 2 = current, 3 = both

  static Mesh meshArea;
  static float[] verticesArea;
  static TextureRegion textureRegionArea;
  static Material materialArea;

  static Mesh meshCurrent;
  static float[] verticesCurrent;
  static TextureRegion textureRegionCurrent;
  static Material materialCurrent;


  public static void update() {
    if (Keybinds.isJustPressed(Keybinds.KEYBIND_AREABOUNDARIES)) {
      state++;
      if (state >= 4) state = 0;
    }
  }

  public static Renderable drawArea(int areaX, int areaY, int areaZ) {
    if (state == 0 || state == 2) return null;
    if (meshArea == null) {
      textureRegionArea = Assets.getTextureRegion("core:world/area_boundary.png");
      materialArea = Assets.getMaterial("core:world/area_boundary.png");

      meshArea = new Mesh(false, 24, 0, CubesVertexAttributes.VERTEX_ATTRIBUTES);
      int vertexOffset = 0;
      verticesArea = new float[CubesVertexAttributes.COMPONENTS * 24];
      float s = Area.SIZE_BLOCKS;

      vertexOffset = createVerticesArea(verticesArea, vertexOffset, 0, 0);
      vertexOffset = createVerticesArea(verticesArea, vertexOffset, 0, s);
      vertexOffset = createVerticesArea(verticesArea, vertexOffset, s, 0);
      vertexOffset = createVerticesArea(verticesArea, vertexOffset, s, s);

      vertexOffset = createVerticesArea(verticesArea, vertexOffset, 0, s, 0, 0, 0);
      vertexOffset = createVerticesArea(verticesArea, vertexOffset, 0, 0, 0, s, 0);
      vertexOffset = createVerticesArea(verticesArea, vertexOffset, 0, s, s, s, 0);
      vertexOffset = createVerticesArea(verticesArea, vertexOffset, s, s, 0, s, 0);

      vertexOffset = createVerticesArea(verticesArea, vertexOffset, 0, s, 0, 0, Area.SIZE_BLOCKS);
      vertexOffset = createVerticesArea(verticesArea, vertexOffset, 0, 0, 0, s, Area.SIZE_BLOCKS);
      vertexOffset = createVerticesArea(verticesArea, vertexOffset, 0, s, s, s, Area.SIZE_BLOCKS);
      vertexOffset = createVerticesArea(verticesArea, vertexOffset, s, s, 0, s, Area.SIZE_BLOCKS);

      meshArea.setVertices(verticesArea);
    }
    CubesRenderable renderable = new CubesRenderable();
    renderable.worldTransform.translate(areaX * Area.SIZE_BLOCKS, areaY * Area.SIZE_BLOCKS, areaZ * Area.SIZE_BLOCKS);
    renderable.meshPart.primitiveType = GL20.GL_LINES;
    renderable.meshPart.offset = 0;
    renderable.meshPart.size = 24;
    renderable.meshPart.mesh = meshArea;
    renderable.material = materialArea;
    renderable.setFogEnabled(false);
    renderable.name = "Area Boundaries";
    return renderable;
  }

  private static int createVerticesArea(float[] vertices, int vertexOffset, float x, float z) {
    vertices[vertexOffset++] = x;
    vertices[vertexOffset++] = 0;
    vertices[vertexOffset++] = z;
    vertices[vertexOffset++] = textureRegionArea.getU();
    vertices[vertexOffset++] = textureRegionArea.getV();
    vertices[vertexOffset++] = BlockLight.FULL_LIGHT;

    vertices[vertexOffset++] = x;
    vertices[vertexOffset++] = Area.SIZE_BLOCKS;
    vertices[vertexOffset++] = z;
    vertices[vertexOffset++] = textureRegionArea.getU2();
    vertices[vertexOffset++] = textureRegionArea.getV2();
    vertices[vertexOffset++] = BlockLight.FULL_LIGHT;
    return vertexOffset;
  }

  private static int createVerticesArea(float[] vertices, int vertexOffset, float x1, float x2, float z1, float z2, float y) {
    vertices[vertexOffset++] = x1;
    vertices[vertexOffset++] = y;
    vertices[vertexOffset++] = z1;
    vertices[vertexOffset++] = textureRegionArea.getU();
    vertices[vertexOffset++] = textureRegionArea.getV();
    vertices[vertexOffset++] = BlockLight.FULL_LIGHT;

    vertices[vertexOffset++] = x2;
    vertices[vertexOffset++] = y;
    vertices[vertexOffset++] = z2;
    vertices[vertexOffset++] = textureRegionArea.getU2();
    vertices[vertexOffset++] = textureRegionArea.getV2();
    vertices[vertexOffset++] = BlockLight.FULL_LIGHT;
    return vertexOffset;
  }

  public static Renderable drawCurrent(int areaX, int areaY, int areaZ) {
    if (state == 0 || state == 1) return null;
    int vertices = ((Area.SIZE_BLOCKS + 1) * 12) * 2;
    if (meshCurrent == null) {
      textureRegionCurrent = Assets.getTextureRegion("core:world/current_area_boundary.png");
      materialCurrent = Assets.getMaterial("core:world/current_area_boundary.png");

      meshCurrent = new Mesh(false, vertices, 0, CubesVertexAttributes.VERTEX_ATTRIBUTES);
      int vertexOffset = 0;
      verticesCurrent = new float[CubesVertexAttributes.COMPONENTS * vertices];
      float s = Area.SIZE_BLOCKS;

      for (int i = 0; i <= Area.SIZE_BLOCKS; i++) {
        // xz horizontal
        vertexOffset = createVerticesCurrent(verticesCurrent, vertexOffset, 0, 0, 0, s, i, i);
        vertexOffset = createVerticesCurrent(verticesCurrent, vertexOffset, s, s, 0, s, i, i);
        vertexOffset = createVerticesCurrent(verticesCurrent, vertexOffset, i, i, 0, s, 0, 0);
        vertexOffset = createVerticesCurrent(verticesCurrent, vertexOffset, i, i, 0, s, s, s);
        // xz vertical
        vertexOffset = createVerticesCurrent(verticesCurrent, vertexOffset, 0, s, i, i, 0, 0);
        vertexOffset = createVerticesCurrent(verticesCurrent, vertexOffset, 0, s, i, i, s, s);
        vertexOffset = createVerticesCurrent(verticesCurrent, vertexOffset, 0, 0, i, i, 0, s);
        vertexOffset = createVerticesCurrent(verticesCurrent, vertexOffset, s, s, i, i, 0, s);

        // y
        vertexOffset = createVerticesCurrent(verticesCurrent, vertexOffset, 0, s, 0, 0, i, i);
        vertexOffset = createVerticesCurrent(verticesCurrent, vertexOffset, 0, s, s, s, i, i);
        vertexOffset = createVerticesCurrent(verticesCurrent, vertexOffset, i, i, 0, 0, 0, s);
        vertexOffset = createVerticesCurrent(verticesCurrent, vertexOffset, i, i, s, s, 0, s);
      }

      meshCurrent.setVertices(verticesCurrent);
    }
    CubesRenderable renderable = new CubesRenderable();
    renderable.worldTransform.translate(areaX * Area.SIZE_BLOCKS, areaY * Area.SIZE_BLOCKS, areaZ * Area.SIZE_BLOCKS);
    renderable.meshPart.primitiveType = GL20.GL_LINES;
    renderable.meshPart.offset = 0;
    renderable.meshPart.size = vertices;
    renderable.meshPart.mesh = meshCurrent;
    renderable.material = materialCurrent;
    renderable.setFogEnabled(false);
    renderable.name = "Current Area Boundaries";
    return renderable;
  }

  private static int createVerticesCurrent(float[] vertices, int vertexOffset, float x1, float x2, float y1, float y2, float z1, float z2) {
    vertices[vertexOffset++] = x1;
    vertices[vertexOffset++] = y1;
    vertices[vertexOffset++] = z1;
    vertices[vertexOffset++] = textureRegionCurrent.getU();
    vertices[vertexOffset++] = textureRegionCurrent.getV();
    vertices[vertexOffset++] = BlockLight.FULL_LIGHT;

    vertices[vertexOffset++] = x2;
    vertices[vertexOffset++] = y2;
    vertices[vertexOffset++] = z2;
    vertices[vertexOffset++] = textureRegionCurrent.getU2();
    vertices[vertexOffset++] = textureRegionCurrent.getV2();
    vertices[vertexOffset++] = BlockLight.FULL_LIGHT;
    return vertexOffset;
  }
}
