package ethanjones.cubes.block;

import ethanjones.cubes.core.util.BlockFace;
import ethanjones.cubes.graphics.world.BlockTextureHandler;
import ethanjones.cubes.item.ItemTool.ToolType;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.storage.Area;

public class BlockGrass extends Block {

  public BlockGrass() {
    super("core:grass");

    miningTime = 0.6f;
    miningTool = ToolType.shovel;
    miningOther = true;
  }

  @Override
  public void loadGraphics() {
    BlockTextureHandler handler = new BlockTextureHandler("core:grass_side");
    handler.setSide(BlockFace.posY, "core:grass_top");
    handler.setSide(BlockFace.negY, "core:dirt");
    textureHandlers = new BlockTextureHandler[]{handler};
  }

  @Override
  public void randomTick(World world, Area area, int x, int y, int z, int meta) {
    if (y < area.maxY) {
      Block above = area.getBlock(x, y + 1, z);
      if (above != null) {
        int aboveMeta = area.getMeta(x, y + 1, z);
        if (!above.isTransparent(aboveMeta)) {
          area.setBlock(Blocks.dirt, x, y, z, 0);
        }
      }
    }
  }
}
