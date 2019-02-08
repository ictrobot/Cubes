package ethanjones.cubes.item;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.event.entity.living.player.PlayerPlaceBlockEvent;
import ethanjones.cubes.core.util.BlockFace;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.graphics.hud.inv.BlockIcons;
import ethanjones.cubes.graphics.world.block.BlockRenderType;
import ethanjones.cubes.input.ClickType;
import ethanjones.cubes.item.inv.InventoryHelper;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.common.Side;
import ethanjones.cubes.world.collision.BlockIntersection;
import ethanjones.cubes.world.reference.BlockReference;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ItemBlock extends Item {

  public final Block block;

  public ItemBlock(Block block) {
    super(block.id);
    this.block = block;
  }

  public void loadGraphics() {

  }

  @Override
  public TextureRegion getTextureRegion(int meta) {
    if (block.renderType(meta) == BlockRenderType.DEFAULT) {
      TextureRegion icon = BlockIcons.getIcon(block.id, meta);
      if (icon != null) return icon;
    }
    return block.getTextureHandler(meta).getSide(BlockFace.posX);
  }

  @Override
  public String getName(int meta) {
    return block.getName(meta);
  }

  @Override
  public boolean onButtonPress(ClickType type, ItemStack itemStack, Player player, int stack) {
    if (Side.isServer() && type == ClickType.place) {
      BlockIntersection blockIntersection = BlockIntersection.getBlockIntersection(player.position, player.angle, Cubes.getServer().world);
      if (blockIntersection == null || blockIntersection.getBlockFace() == null) return false;
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
      // check block would not be in player
      if (blockReference.equals(new BlockReference().setFromVector3(player.position))) return false;
      if (blockReference.equals(new BlockReference().setFromVector3(player.position.cpy().sub(0, player.height, 0))))
        return false;


      Integer meta = block.place(Cubes.getServer().world, blockReference.blockX, blockReference.blockY, blockReference.blockZ, itemStack.meta, player, blockIntersection);
      if (meta == null) return false;

      PlayerPlaceBlockEvent event = new PlayerPlaceBlockEvent(player, block, meta, blockIntersection, blockReference);
      if (event.post().isCanceled()) return false;

      Cubes.getServer().world.setBlock(block, blockReference.blockX, blockReference.blockY, blockReference.blockZ, event.getMeta());

      InventoryHelper.reduceCount(player.getInventory(), stack);
      return true;
    }
    return false;
  }
}
