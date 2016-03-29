package ethanjones.cubes.entity.living.player;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.entity.living.LivingEntity;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packets.PacketChat;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.server.command.CommandPermission;
import ethanjones.cubes.side.server.command.CommandSender;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.World;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

public class Player extends LivingEntity implements CommandSender {

  public final String username;
  public final ClientIdentifier clientIdentifier;

  private final PlayerInventory inventory;
  private int selectedSlot;
  private Vector3 previousPosition = new Vector3();

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

  public void addToWorld() {
    World world = Sided.getCubes().world;
    world.lock.writeLock();
    world.entities.put(uuid, this);
    world.lock.writeUnlock();
  }

  @Override
  public boolean update() {
    if (Sided.getSide() == Side.Server) {
      World world = Sided.getCubes().world;
      if (world.getArea(CoordinateConverter.area(position.x), CoordinateConverter.area(position.z)) != null) {
        if (world.getBlock((int) position.x, (int) (position.y - height), (int) position.z) != null) {
          position.set(previousPosition);
          world.syncEntity(uuid);
        }
      }
      previousPosition.set(position);
    }
    return super.update();
  }
}
