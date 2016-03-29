package ethanjones.cubes.item;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.util.BlockFace;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.graphics.world.RayTracing;
import ethanjones.cubes.item.inv.InventoryHelper;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.reference.BlockReference;

public class ItemBlock extends Item {
  public final Block block;

  public ItemBlock(Block block) {
    super(block.id);
    this.block = block;
  }

  public void loadGraphics() {
    this.texture = block.getTextureHandler().getSide(BlockFace.posX);
  }

  @Override
  public void onButtonPress(int button, ItemStack itemStack, Player player, int stack) {
    if (Sided.getSide() == Side.Server) {
      RayTracing.BlockIntersection blockIntersection = RayTracing.getBlockIntersection(player.position, player.angle, Cubes.getServer().world);
      if (blockIntersection != null) {
        BlockReference blockReference = blockIntersection.getBlockReference();
        switch (blockIntersection.getBlockFace()) {
          case posX:
            blockReference.blockX++;
            break;
          case negX:
            blockReference.blockX--;
            break;
          case posY:
            blockReference.blockY++;
            break;
          case negY:
            blockReference.blockY--;
            break;
          case posZ:
            blockReference.blockZ++;
            break;
          case negZ:
            blockReference.blockZ--;
            break;
        }
        Cubes.getServer().world.setBlock(block, blockReference.blockX, blockReference.blockY, blockReference.blockZ);

        InventoryHelper.reduceCount(player.getInventory(), stack);
      }
    }
  }
}
