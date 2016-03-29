package ethanjones.cubes.entity.living.player;

import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.entity.living.LivingEntity;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packets.PacketChat;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.side.server.command.CommandPermission;
import ethanjones.cubes.side.server.command.CommandSender;

import com.badlogic.gdx.graphics.Camera;

public class Player extends LivingEntity implements CommandSender {

  public final String username;
  public final ClientIdentifier clientIdentifier;

  private final PlayerInventory inventory;
  private int selectedSlot;

  public Player(String username, ClientIdentifier clientIdentifier) {
    super("core:player", 20);
    this.username = username;
    this.clientIdentifier = clientIdentifier;
    this.inventory = new PlayerInventory(this);
    this.height = 1.5f;
  }

  public Player(Camera camera) {
    super("core:player", camera.position, camera.direction, 20);
    this.username = Settings.getStringSettingValue(Settings.USERNAME);
    this.clientIdentifier = null;
    this.inventory = new PlayerInventory(this);
    this.height = 1.5f;
  }

  public PlayerInventory getInventory() {
    return inventory;
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
