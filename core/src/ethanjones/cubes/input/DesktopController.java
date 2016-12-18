package ethanjones.cubes.input;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.ItemTool;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packets.PacketClick;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.collision.BlockIntersection;
import ethanjones.cubes.world.reference.BlockReference;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;

public class DesktopController extends InputAdapter {
  private ClickType current = ClickType.none;
  
  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    current = ClickType.type(button);
    NetworkingManager.sendPacketToServer(new PacketClick(current));
    handleClick(current);
    return true;
  }
  
  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    if (button == current.num) {
      NetworkingManager.sendPacketToServer(new PacketClick(ClickType.none));
      current = ClickType.none;
    }
    return true;
  }
  
  public static boolean handleClick(ClickType type) {
    Player player = Cubes.getClient().player;
    ItemStack itemStack = player.getInventory().selectedItemStack();
    Camera camera = Cubes.getClient().renderer.worldRenderer.camera;
    BlockIntersection blockIntersection = BlockIntersection.getBlockIntersection(camera.position, camera.direction, Cubes.getClient().world);
    if (blockIntersection != null) {
      BlockReference r = blockIntersection.getBlockReference();
      Block b = Cubes.getClient().world.getBlock(r.blockX, r.blockY, r.blockZ);
      if (b.onButtonPress(type, player, r.blockX, r.blockY, r.blockZ)) return true;
    }
    return itemStack != null && itemStack.item.onButtonPress(type, itemStack, player, player.getInventory().hotbarSelected);
  }
  
  public void tick() {
    ItemTool.mine(Cubes.getClient().player, current == ClickType.mine);
  }
}
