package ethanjones.cubes.block.blocks;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.block.Blocks;
import ethanjones.cubes.core.util.BlockFace;
import ethanjones.cubes.graphics.world.block.BlockTextureHandler;
import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.ItemTool.ToolType;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.storage.Area;

public class BlockGrass extends Block {

  public static final int MIN_LIGHT = 10;

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
  public ItemStack[] drops(World world, int x, int y, int z, int meta) {
    return new ItemStack[]{new ItemStack(Blocks.dirt.getItemBlock(), 1, 0)};
  }

  @Override
  public void randomTick(World world, Area area, int x, int y, int z, int meta) {
    if (y < area.maxY && area.getMaxLight(x, y + 1, z) < MIN_LIGHT) {
      area.setBlock(Blocks.dirt, x, y, z, 0);
    }
  }
}
