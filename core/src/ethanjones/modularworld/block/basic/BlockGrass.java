package ethanjones.modularworld.block.basic;

import ethanjones.modularworld.block.Block;
import ethanjones.modularworld.core.util.Direction;

public class BlockGrass extends Block {
  public BlockGrass() {
    super("core:block/Grass - Side.png");
  }

  public void loadGraphics() {
    super.loadGraphics();
    this.textureHandler.setSide(Direction.posY, "core:block/Grass.png").setSide(Direction.negY, "core:block/Dirt.png");
  }
}
