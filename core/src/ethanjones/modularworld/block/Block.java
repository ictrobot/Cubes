package ethanjones.modularworld.block;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import ethanjones.modularworld.data.ByteData;
import ethanjones.modularworld.graphics.GraphicsHelper;

/**
 * Actual Block in World
 */
public class Block {
  
  public final BlockFactory factory;
  protected ByteData data;
  protected ModelInstance model;

  public Block(BlockFactory factory, ByteData data) {
    this(factory);
    this.data = data;
  }
  
  public Block(BlockFactory factory) {
    this.factory = factory;
    this.data = new ByteData();
  }
  
  protected Material getTexture() {
    return factory.getTexture(data);
  }
  
  public ModelInstance getModelInstance(int x, int y, int z) {
    if (model == null) {
      model = new ModelInstance(GraphicsHelper.getModelBuilder().createBox(1f, 1f, 1f, getTexture(), GraphicsHelper.usage), x, y, z);
    }
    return model;
  }
}
