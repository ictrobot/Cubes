package ethanjones.cubes.item;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.entity.ItemEntity;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.server.PlayerManager;
import ethanjones.cubes.world.collision.BlockIntersection;
import ethanjones.cubes.world.reference.BlockReference;

public class ItemTool extends Item {
  public static enum ToolType {
    pickaxe, axe, shovel;
  }

  public static class MiningTarget {
    float time;
    BlockReference target;
    ItemStack itemStack;
  }

  protected ToolType toolType = ToolType.pickaxe;
  protected int toolLevel = 1;

  public ItemTool(String id) {
    super(id);
  }

  public ToolType getToolType() {
    return toolType;
  }

  public int getToolLevel() {
    return toolLevel;
  }

  public static void mine(Player player) {
    ItemStack itemStack = player.getInventory().selectedItemStack(); // may be null
    BlockIntersection blockIntersection = BlockIntersection.getBlockIntersection(player.position, player.angle, Cubes.getServer().world);
    if (blockIntersection != null) {
      BlockReference blockReference = blockIntersection.getBlockReference();
      Block block = Cubes.getServer().world.getBlock(blockReference.blockX, blockReference.blockY, blockReference.blockZ);

      if (block != null) {
        PlayerManager playerManager = player.clientIdentifier.getPlayerManager();
        MiningTarget target = playerManager.getCurrentlyMining();
        if (target == null || !blockReference.equals(target.target) || target.itemStack != itemStack) {
          target = new MiningTarget();
          target.target = blockReference;
          target.itemStack = itemStack;
          playerManager.setCurrentlyMining(target);
        }
        target.time += block.getMiningSpeed(itemStack) * (Cubes.tickMS / 1000f);
        if (target.time >= block.getMiningTime()) {
          Cubes.getServer().world.setBlock(null, blockReference.blockX, blockReference.blockY, blockReference.blockZ);
          if (block.canMine(itemStack)) {
            ItemEntity itemEntity = new ItemEntity();
            itemEntity.itemStack = new ItemStack(block.getItemBlock(), 1);
            itemEntity.position.set(blockReference.blockX + 0.5f, blockReference.blockY, blockReference.blockZ + 0.5f);
            Cubes.getServer().world.addEntity(itemEntity);
          }
        }
      }
    }
  }

  @Override
  public int getStackCountMax() {
    return 1;
  }
}
