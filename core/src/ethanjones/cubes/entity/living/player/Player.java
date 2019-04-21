package ethanjones.cubes.entity.living.player;

import ethanjones.cubes.core.event.entity.living.player.PlayerMovementEvent;
import ethanjones.cubes.core.gwt.UUID;
import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.entity.living.LivingEntity;
import ethanjones.cubes.graphics.entity.PlayerRenderer;
import ethanjones.cubes.item.ItemTool;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packets.PacketChat;
import ethanjones.cubes.networking.packets.PacketPlayerNoClip;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.side.client.CubesClient;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.common.Side;
import ethanjones.cubes.side.server.command.CommandPermission;
import ethanjones.cubes.side.server.command.CommandSender;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.gravity.WorldGravity;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.server.LoadedAreaFilter;
import ethanjones.cubes.world.server.WorldServer;
import ethanjones.data.DataGroup;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class Player extends LivingEntity implements CommandSender, RenderableProvider, LoadedAreaFilter {

  public static final float PLAYER_HEIGHT = 1.625f;
  public static final float PLAYER_RADIUS = 0.24f;

  public final String username;
  public final ClientIdentifier clientIdentifier;

  private final PlayerInventory inventory;
  private Vector3 previousPosition = new Vector3();
  private ItemTool.MiningTarget currentlyMining;

  private boolean noClip = false;

  public Player(String username, UUID uuid) {
    super("core:player", 20);
    this.uuid = uuid;
    this.username = username;
    this.clientIdentifier = null;
    this.inventory = new PlayerInventory(this);
    this.height = PLAYER_HEIGHT;
  }

  public Player(String username, UUID uuid, ClientIdentifier clientIdentifier) {
    super("core:player", 20);
    this.uuid = uuid;
    this.username = username;
    this.clientIdentifier = clientIdentifier;
    this.inventory = new PlayerInventory(this);
    this.height = PLAYER_HEIGHT;
  }

  public Player(Camera camera) {
    super("core:player", camera.position, camera.direction, 20);
    this.uuid = CubesClient.uuid;
    this.username = Settings.getStringSettingValue(Settings.USERNAME);
    this.clientIdentifier = null;
    this.inventory = new PlayerInventory(this);
    this.height = PLAYER_HEIGHT;
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

  @Override
  public Vector3 getLocation() throws UnsupportedOperationException {
    return position;
  }

  public void addToWorld() {
    World world = Side.getCubes().world;
    world.entities.lock.writeLock();
    world.entities.put(uuid, this);
    if (world instanceof WorldServer) ((WorldServer) world).addLoadedAreaFilter(this);
    world.entities.lock.writeUnlock();
  }

  @Override
  public void updatePosition(float time) {
    if (!noClip) {
      if (Side.isClient() && !Cubes.getClient().inputChain.cameraController.flying()) {
        if (!inLoadedArea()) return;
        Side side = Side.getSide();
        World world = Side.getCubes().world;
        tmpVector.set(position);

        if (!motion.isZero() || !WorldGravity.onBlock(world, tmpVector, height, PLAYER_RADIUS)) {
          tmpVector.add(motion.x * time, motion.y * time, motion.z * time);
          motion.y -= GRAVITY * time;

          if (WorldGravity.onBlock(world, tmpVector, height, PLAYER_RADIUS) && motion.y < 0) {
            tmpVector.y = WorldGravity.getBlockY(tmpVector, height) + 1 + height;
            motion.y = 0f;
          }

          if (!new PlayerMovementEvent(this, tmpVector).post().isCanceled()) {
            position.set(tmpVector);
            if (side == Side.Server) world.syncEntity(uuid);
          }
        }
      }
      World world = Side.getCubes().world;
      if (world.getArea(CoordinateConverter.area(position.x), CoordinateConverter.area(position.z)) != null) {
        if (world.getBlock(CoordinateConverter.block(position.x), CoordinateConverter.block(position.y - height), CoordinateConverter.block(position.z)) != null) {
          position.set(previousPosition);
          world.syncEntity(uuid);
        }
      }
    }
    previousPosition.set(position);
  }

  @Override
  public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
    if (Side.isServer() || this == Cubes.getClient().player) return;
    PlayerRenderer.getRenderables(renderables, pool, this);
  }


  public ItemTool.MiningTarget getCurrentlyMining() {
    return currentlyMining;
  }

  public void setCurrentlyMining(ItemTool.MiningTarget currentlyMining) {
    this.currentlyMining = currentlyMining;
  }

  public DataGroup write() {
    DataGroup dataGroup = super.write();
    dataGroup.put("username", username);
    dataGroup.put("inventory", inventory.write());
    dataGroup.put("noClip", noClip);
    return dataGroup;
  }


  public void read(DataGroup dataGroup) {
    super.read(dataGroup);
    if (!uuid.equals(dataGroup.get("uuid"))) {
      throw new IllegalStateException("Player uuid does not match");
    }
    inventory.read(dataGroup.getGroup("inventory"));
    noClip = dataGroup.getBoolean("noClip");
  }

  @Override
  public boolean load(AreaReference a) {
    return clientIdentifier.getPlayerManager().areaInLoadRange(a);
  }

  public boolean noClip() {
    return noClip;
  }

  public boolean isNoClipInBlock() {
    if (!noClip) return false;
    World world = Side.getCubes().world;
    return world.getBlock(CoordinateConverter.block(position.x), CoordinateConverter.block(position.y), CoordinateConverter.block(position.z)) != null ||
        world.getBlock(CoordinateConverter.block(position.x), CoordinateConverter.block(position.y - 1), CoordinateConverter.block(position.z)) != null;
  }

  public void setNoClip(boolean enabled) {
    if (Side.isServer()) {
      if (isNoClipInBlock()) {
        print(Localization.get("command.noclip.tpSpawn"));
        clientIdentifier.getPlayerManager().teleportToSpawn();
      }

      PacketPlayerNoClip p = new PacketPlayerNoClip();
      p.enabled = enabled;
      NetworkingManager.sendPacketToClient(p, clientIdentifier);
    }
    this.noClip = enabled;
  }
}
