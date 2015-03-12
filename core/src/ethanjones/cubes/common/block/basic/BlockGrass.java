package ethanjones.cubes.common.block.basic;

import ethanjones.cubes.common.block.Block;
import ethanjones.cubes.common.core.util.BlockFace;

public class BlockGrass extends Block {

  public BlockGrass() {
    super("core:grass");
  }

  public void loadGraphics() {
    super.loadGraphics();

    textureHandler.setSide(BlockFace.posY, "core:block/grass.png");
    textureHandler.setSide(BlockFace.negY, "core:block/dirt.png");

    textureHandler.setSide(BlockFace.posX, "core:block/grass_side.png");
    textureHandler.setSide(BlockFace.negX, "core:block/grass_side.png");

    textureHandler.setSide(BlockFace.posZ, "core:block/grass_side.png");
    textureHandler.setSide(BlockFace.negZ, "core:block/grass_side.png");
  }
}
