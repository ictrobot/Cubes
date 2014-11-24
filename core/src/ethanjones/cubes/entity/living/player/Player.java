package ethanjones.cubes.entity.living.player;

import com.badlogic.gdx.graphics.Camera;

import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.entity.living.LivingEntity;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packets.PacketChat;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.side.server.command.CommandPermission;
import ethanjones.cubes.side.server.command.CommandSender;

public class Player extends LivingEntity implements CommandSender {

  public final String username;
  private final ClientIdentifier clientIdentifier;

  public Player(String username, ClientIdentifier clientIdentifier) {
    super(20);
    this.username = username;
    this.clientIdentifier = clientIdentifier;
  }

  public Player(Camera camera) {
    super(camera.position, camera.direction, 20);
    this.username = Settings.getStringSettingValue(Settings.USERNAME);
    this.clientIdentifier = null;
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
