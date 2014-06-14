package ethanjones.modularworld.block.basic;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import ethanjones.modularworld.block.BlockFactory;
import ethanjones.modularworld.data.ByteData;
import ethanjones.modularworld.graphics.GameModel;
import ethanjones.modularworld.graphics.GraphicsHelper;

public class BlockFactoryBasic extends BlockFactory {
  
  Material material;
  Model model;
  GameModel gameModel;
  
  public BlockFactoryBasic(String id) {
    super(id);
  }
  
  @Override
  public void loadGraphics() {
    material = GraphicsHelper.load("Blocks/" + id + ".png");
    model = GraphicsHelper.getModelBuilder().createBox(1f, 1f, 1f, material, GraphicsHelper.usage);
    gameModel = new GameModel(model);
  }
  
  @Override
  public Material getTexture(ByteData data) {
    return material;
  }
  
  @Override
  public Model getModel(ByteData data) {
    return model;
  }
  
  @Override
  public GameModel getModelInstance(ByteData data) {
    return gameModel;
  }
  
}
