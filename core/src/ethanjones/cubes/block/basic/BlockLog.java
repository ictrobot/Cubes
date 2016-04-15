package ethanjones.cubes.block.basic;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.util.BlockFace;

public class BlockLog extends Block {

  public BlockLog() {
    super("core:log");
  }

  public void loadGraphics() {
    super.loadGraphics();

    textureHandler.setSide(BlockFace.posY, "core:block/log_y.png");
    textureHandler.setSide(BlockFace.negY, "core:block/log_y.png");

    textureHandler.setSide(BlockFace.posX, "core:block/log.png");
    textureHandler.setSide(BlockFace.negX, "core:block/log.png");

    textureHandler.setSide(BlockFace.posZ, "core:block/log.png");
    textureHandler.setSide(BlockFace.negZ, "core:block/log.png");
  }
}