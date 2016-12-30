package ethanjones.cubes.item;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.event.entity.living.player.PlayerBreakBlockEvent;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.common.Side;
import ethanjones.cubes.world.collision.BlockIntersection;
import ethanjones.cubes.world.reference.BlockReference;

public class ItemTool extends Item {
  public enum ToolType {
    pickaxe, axe, shovel, none
  }

  public static class MiningTarget {
    public float time;
    public float totalTime;
    public BlockReference target;
    ItemStack itemStack;
  }

  protected ToolType toolType = ToolType.none;
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

  @Override
  public int getStackCountMax() {
    return 1;
  }

  public static void mine(Player player, boolean mine) {
    if (mine) {
      ItemStack itemStack = player.getInventory().selectedItemStack(); // may be null
      BlockIntersection blockIntersection = BlockIntersection.getBlockIntersection(player.position, player.angle, Side.getCubes().world);
      if (blockIntersection != null) {
        BlockReference blockReference = blockIntersection.getBlockReference();
        Block block = Side.getCubes().world.getBlock(blockReference.blockX, blockReference.blockY, blockReference.blockZ);
        int meta = Side.getCubes().world.getMeta(blockReference.blockX, blockReference.blockY, blockReference.blockZ);

        if (block != null) {
          MiningTarget target = player.getCurrentlyMining();
          if (target == null || !blockReference.equals(target.target) || target.itemStack != itemStack) {
            target = new MiningTarget();
            target.target = blockReference;
            target.totalTime = block.getMiningTime();
            target.itemStack = itemStack;
            player.setCurrentlyMining(target);
          }
          target.time += block.getMiningSpeed(itemStack) * (Cubes.tickMS / 1000f);
          if (target.time >= target.totalTime) {
            player.setCurrentlyMining(null);
            if (Side.isServer()) {
              Cubes.getServer().world.setBlock(null, blockReference.blockX, blockReference.blockY, blockReference.blockZ);
              if (block.canMine(itemStack)) {
                PlayerBreakBlockEvent event = new PlayerBreakBlockEvent(player, block, meta, blockIntersection, blockReference);
                if (event.post().isCanceled()) return;
                block.dropItems(Cubes.getServer().world, blockReference.blockX, blockReference.blockY, blockReference.blockZ, meta);
              }
            }
          }
          return;
        }
      }
    }
    player.setCurrentlyMining(null);
  }
}
