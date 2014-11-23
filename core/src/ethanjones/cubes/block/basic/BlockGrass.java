package ethanjones.cubes.block.basic;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.util.BlockFace;

public class BlockGrass extends Block {

  public BlockGrass() {
    super("core:block/Grass - Side.png");
  }

  public void loadGraphics() {
    super.loadGraphics();
    this.textureHandler.setSide(BlockFace.posY, "core:block/Grass.png").setSide(BlockFace.negY, "core:block/Dirt.png");
  }
}
