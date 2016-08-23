package ethanjones.cubes.block;

import ethanjones.cubes.block.data.BlockData;
import ethanjones.cubes.block.data.BlockDataChest;
import ethanjones.cubes.core.util.BlockFace;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.graphics.hud.inv.DoubleInventory;
import ethanjones.cubes.graphics.hud.inv.InventoryActor;
import ethanjones.cubes.graphics.hud.inv.InventoryManager;
import ethanjones.cubes.graphics.hud.inv.InventoryWindow;
import ethanjones.cubes.graphics.world.BlockTextureHandler;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.storage.Area;
import ethanjones.data.DataGroup;

public class BlockChest extends Block {

  private static final BlockFace[] lockFace = new BlockFace[]{BlockFace.posX, BlockFace.negX, BlockFace.posZ, BlockFace.negZ};

  public BlockChest() {
    super("core:chest");
  }

  @Override
  public void loadGraphics() {
    textureHandlers = new BlockTextureHandler[4];

    for (int i = 0; i < textureHandlers.length; i++) {
      textureHandlers[i] = new BlockTextureHandler("core:chest_side");
      textureHandlers[i].setSide(BlockFace.posY, "core:chest_y");
      textureHandlers[i].setSide(BlockFace.negY, "core:chest_y");
      textureHandlers[i].setSide(lockFace[i], "core:chest_lock");
    }
  }

  @Override
  public boolean blockData() {
    return true;
  }

  @Override
  public BlockData createBlockData(Area area, int x, int y, int z, int meta, DataGroup dataGroup) {
    return new BlockDataChest(area, x, y, z);
  }

  @Override
  public boolean onButtonPress(int button, Player player, int blockX, int blockY, int blockZ) {
    if (Sided.getSide() == Side.Server) return false;
    BlockData blockData = Sided.getCubes().world.getBlockData(blockX, blockY, blockZ);
    if (blockData instanceof BlockDataChest) {
      InventoryActor inventoryActor = new InventoryActor(((BlockDataChest) blockData).inventory);
      InventoryActor playerInv = Cubes.getClient().renderer.guiRenderer.playerInv;
      InventoryManager.showInventory(new InventoryWindow(new DoubleInventory(inventoryActor, playerInv)));
    }
    return true;
  }
}
