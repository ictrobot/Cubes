package ethanjones.cubes.entity.living.player;

import com.badlogic.gdx.graphics.Camera;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.block.Blocks;
import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.entity.living.LivingEntity;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packets.PacketChat;
import ethanjones.cubes.networking.packets.PacketHotbar;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.server.command.CommandPermission;
import ethanjones.cubes.side.server.command.CommandSender;

public class Player extends LivingEntity implements CommandSender {

  public static final int HOTBAR_SIZE = 10;

  public final String username;
  public final ClientIdentifier clientIdentifier;

  private final Block[] hotbar;
  private int selectedSlot;

  public Player(String username, ClientIdentifier clientIdentifier) {
    super(20);
    this.username = username;
    this.clientIdentifier = clientIdentifier;
    this.hotbar = new Block[HOTBAR_SIZE];
    this.hotbar[0] = Blocks.grass;
  }

  public Player(Camera camera) {
    super(camera.position, camera.direction, 20);
    this.username = Settings.getStringSettingValue(Settings.USERNAME);
    this.clientIdentifier = null;
    this.hotbar = new Block[HOTBAR_SIZE];
    this.hotbar[0] = Blocks.grass;
  }

  public Block getHotbar(int i) {
    return hotbar[i];
  }

  public Block getHotbarSelected() {
    return hotbar[selectedSlot];
  }

  public int getSelectedSlot() {
    return selectedSlot;
  }

  public void setHotbar(Block block) {
    hotbar[selectedSlot] = block;
    updateHotbar();
  }

  public void setHotbar(int i, Block block) {
    hotbar[i] = block;
    updateHotbar();
  }

  public void setSelectedSlot(int i) {
    selectedSlot = i;
    updateHotbar();
  }

  public void setHotbarNoUpdate(Block[] blocks, int i) {
    for (int x = 0; x < HOTBAR_SIZE; x++) {
      this.hotbar[x] = blocks[x];
    }
    selectedSlot = i;
  }

  private void updateHotbar() {
    PacketHotbar packetHotbar = new PacketHotbar();
    packetHotbar.blocks = hotbar;
    packetHotbar.selected = selectedSlot;
    if (Sided.getSide() == Side.Client) {
      NetworkingManager.sendPacketToServer(packetHotbar);
    } else {
      NetworkingManager.sendPacketToClient(packetHotbar, clientIdentifier);
    }
  }

  @Override
  public void print(String string) {
    if (clientIdentifier != null) {
      PacketChat packet = new PacketChat();
      packet.msg = string;
      NetworkingManager.sendPacketToClient(packet, clientIdentifier);
    }
  }

  @Override
  public CommandPermission getPermissionLevel() {
    return CommandPermission.Extended;
  }
}
