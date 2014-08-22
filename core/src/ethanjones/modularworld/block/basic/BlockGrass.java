package ethanjones.modularworld.block.basic;

import ethanjones.modularworld.block.Block;
import ethanjones.modularworld.core.util.Direction;

public class BlockGrass extends Block {
  public BlockGrass() {
    super("Grass - Side");
  }

  public void loadGraphics() {
    super.loadGraphics();
    this.textureHandler.setSide(Direction.posY, "Grass").setSide(Direction.negY, "Dirt");
  }
}
