package ethanjones.modularworld.block.basic;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import ethanjones.modularworld.block.BlockFactory;
import ethanjones.modularworld.data.ByteData;
import ethanjones.modularworld.graphics.GraphicsHelper;

public class BlockFactoryBasic extends BlockFactory {
  
  Material texture;
  Model model;
  
  public BlockFactoryBasic(String id) {
    super(id);
  }
  
  @Override
  public void loadGraphics() {
    texture = GraphicsHelper.load("Blocks/" + id + ".png");
    model = GraphicsHelper.getModelBuilder().createBox(1f, 1f, 1f, texture, GraphicsHelper.usage);
  }
  
  @Override
  public Material getTexture(ByteData data) {
    return texture;
  }
  
  @Override
  public Model getModel(ByteData data) {
    return model;
  }
  
}
