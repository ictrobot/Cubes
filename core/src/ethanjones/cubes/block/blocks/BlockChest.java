package ethanjones.cubes.block.blocks;

import com.badlogic.gdx.math.Vector3;
import ethanjones.cubes.block.Block;
import ethanjones.cubes.block.data.BlockData;
import ethanjones.cubes.block.data.BlockDataChest;
import ethanjones.cubes.core.util.BlockFace;
import ethanjones.cubes.core.util.VectorUtil;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.graphics.hud.inv.DoubleInventory;
import ethanjones.cubes.graphics.hud.inv.InventoryActor;
import ethanjones.cubes.graphics.hud.inv.InventoryManager;
import ethanjones.cubes.graphics.hud.inv.InventoryWindow;
import ethanjones.cubes.graphics.world.block.BlockTextureHandler;
import ethanjones.cubes.input.ClickType;
import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.ItemTool.ToolType;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.common.Side;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.collision.BlockIntersection;
import ethanjones.cubes.world.storage.Area;
import ethanjones.data.DataGroup;

public class BlockChest extends Block {

  private static final BlockFace[] lockFace = new BlockFace[]{BlockFace.posX, BlockFace.negX, BlockFace.posZ, BlockFace.negZ};

  public BlockChest() {
    super("core:chest");

    miningTime = 3;
    miningTool = ToolType.axe;
    miningOther = true;
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
  public boolean onButtonPress(ClickType type, Player player, int blockX, int blockY, int blockZ) {
    if (Side.isServer() || type != ClickType.place) return false;
    BlockData blockData = Side.getCubes().world.getBlockData(blockX, blockY, blockZ);
    if (blockData instanceof BlockDataChest) {
      InventoryActor inventoryActor = new InventoryActor(((BlockDataChest) blockData).inventory);
      InventoryActor playerInv = Cubes.getClient().renderer.guiRenderer.playerInv;
      InventoryManager.showInventory(new InventoryWindow(new DoubleInventory(inventoryActor, playerInv)));
    }
    return true;
  }

  @Override
  public Integer place(World world, int x, int y, int z, int meta, Player player, BlockIntersection intersection) {
    Vector3 pos = player.position.cpy();
    pos.sub(x, y, z);
    pos.nor();
    BlockFace blockFace = VectorUtil.directionXZ(pos);
    if (blockFace == BlockFace.negX) {
      return 1;
    } else if (blockFace == BlockFace.posZ) {
      return 2;
    } else if (blockFace == BlockFace.negZ) {
      return 3;
    }
    return 0;
  }

  @Override
  public ItemStack[] drops(World world, int x, int y, int z, int meta) {
    return super.drops(world, x, y, z, 0);
  }
}
