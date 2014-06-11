package ethanjones.modularworld.block.basic;

import com.badlogic.gdx.graphics.g3d.Material;
import ethanjones.modularworld.block.BlockFactory;
import ethanjones.modularworld.data.ByteData;
import ethanjones.modularworld.graphics.GraphicsHelper;

public class BlockFactoryBasic extends BlockFactory {
  
  Material normal;
  
  public BlockFactoryBasic(String id) {
    super(id);
  }
  
  @Override
  public void loadTextures() {
    normal = GraphicsHelper.load("Blocks/" + id + ".png");
  }
  
  @Override
  public Material getTexture(ByteData data) {
    return normal;
  }
  
}
