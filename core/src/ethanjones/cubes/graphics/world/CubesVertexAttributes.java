package ethanjones.cubes.graphics.world;

import ethanjones.cubes.graphics.world.ao.AmbientOcclusion;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class CubesVertexAttributes {

  public static final VertexAttributes VERTEX_ATTRIBUTES;
  public static final VertexAttributes VERTEX_ATTRIBUTES_AO;

  static {
    VertexAttribute[] vertexAttributes = new VertexAttribute[3];
    vertexAttributes[0] = new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE);
    vertexAttributes[1] = new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0");
    vertexAttributes[2] = new VertexAttribute(VertexAttributes.Usage.Generic, 1, "a_voxellight", 0);
    VERTEX_ATTRIBUTES = new VertexAttributes(vertexAttributes);

    VertexAttribute[] vertexAttributesAO = new VertexAttribute[4];
    vertexAttributesAO[0] = new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE);
    vertexAttributesAO[1] = new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0");
    vertexAttributesAO[2] = new VertexAttribute(VertexAttributes.Usage.Generic, 1, "a_voxellight", 0);
    vertexAttributesAO[3] = new VertexAttribute(VertexAttributes.Usage.Generic, 2, "a_ao_texCoord", 1);
    VERTEX_ATTRIBUTES_AO = new VertexAttributes(vertexAttributesAO);
  }

  public static final int COMPONENTS = 6;
  public static final int COMPONENTS_AO = 8; //3 for position, 2 for texture coordinates, 1 for light, 2 for ao

  public static VertexAttributes getVertexAttributes() {
    if (AmbientOcclusion.isEnabled()) return VERTEX_ATTRIBUTES_AO;
    return VERTEX_ATTRIBUTES;
  }

  public static int components(VertexAttributes v) {
    if (v == VERTEX_ATTRIBUTES) return COMPONENTS;
    if (v == VERTEX_ATTRIBUTES_AO) return COMPONENTS_AO;
    int components = 0;
    for (VertexAttribute attribute : v) {
      components += attribute.numComponents;
    }
    return components;
  }

}
