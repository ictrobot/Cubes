package ethanjones.modularworld.block.basic;

import ethanjones.modularworld.core.util.Direction;
import ethanjones.modularworld.graphics.GraphicsHelper;

public class BlockFactoryGrass extends BlockFactoryBasic {
  public BlockFactoryGrass() {
    super("Grass", "Grass - Side");
  }

  public void loadGraphics() {
    super.loadGraphics();
    this.textureHandler.setSide(Direction.posY, GraphicsHelper.loadBlock("Grass")).setSide(Direction.negY, GraphicsHelper.loadBlock("Dirt"));
  }
}
