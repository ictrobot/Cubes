package ethanjones.cubes.side.server;

import ethanjones.cubes.core.event.EventHandler;
import ethanjones.cubes.core.event.entity.living.player.PlayerMovementEvent;
import ethanjones.cubes.core.event.world.block.BlockChangedEvent;
import ethanjones.cubes.core.event.world.generation.AreaLoadedEvent;
import ethanjones.cubes.entity.ItemEntity;
import ethanjones.cubes.entity.living.player.PlayerInventory;
import ethanjones.cubes.item.ItemTool;
import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.inv.InventoryHelper;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packets.*;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.reference.BlockReference;
import ethanjones.cubes.world.reference.multi.AreaReferenceSet;
import ethanjones.cubes.world.reference.multi.WorldRegion;
import ethanjones.cubes.world.storage.Area;
import ethanjones.cubes.world.thread.GenerationTask;
import ethanjones.cubes.world.thread.WorldRequestParameter;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

public class PlayerManager {

  public final ClientIdentifier client;
  private final CubesServer server;
  private final AreaReference playerArea;
  private final ArrayList<Integer> keys;
  private final ArrayList<Integer> buttons;
  private final ArrayList<Integer> recentKeys;
  private final ArrayList<Integer> recentButtons;
  private GenerationTask initialGenerationTask;
  private int renderDistance;
  private int loadDistance;

  public PlayerManager(ClientIdentifier clientIdentifier, PacketConnect packetConnect) {
    this.server = Cubes.getServer();
    this.client = clientIdentifier;
    this.playerArea = new AreaReference().setFromPositionVector3(client.getPlayer().position);
    this.keys = new ArrayList<Integer>();
    this.buttons = new ArrayList<Integer>();
    this.recentKeys = new ArrayList<Integer>();
    this.recentButtons = new ArrayList<Integer>();

    renderDistance = packetConnect.renderDistance;
    loadDistance = renderDistance + 1;

    Sided.getEventBus().register(this);

    PacketConnected packetConnected = new PacketConnected();
    packetConnected.idManager = Sided.getIDManager().write();
    packetConnected.player = client.getPlayer().uuid;
    packetConnected.worldTime = server.world.time;
    NetworkingManager.sendPacketToClient(packetConnected, client);

    PacketPlayerInventory packetPlayerInventory = new PacketPlayerInventory();
    packetPlayerInventory.inv = clientIdentifier.getPlayer().getInventory().write();
    NetworkingManager.sendPacketToClient(packetPlayerInventory, client);

    if (clientIdentifier.getPlayer().position.isZero()) teleportToSpawn();

    PacketChat packetChat = new PacketChat(); //TODO server should log connecting and disconnecting messages
    packetChat.msg = packetConnect.username + " joined the game";
    NetworkingManager.sendPacketToAllClients(packetChat);

    PacketOtherPlayerConnected packetOtherPlayerConnected = new PacketOtherPlayerConnected();
    packetOtherPlayerConnected.username = packetConnect.username;
    packetOtherPlayerConnected.uuid = client.getPlayer().uuid;
    packetOtherPlayerConnected.position = client.getPlayer().position;
    packetOtherPlayerConnected.angle = client.getPlayer().angle;
    NetworkingManager.sendPacketToOtherClients(packetOtherPlayerConnected, client);

    for (ClientIdentifier c : Cubes.getServer().getAllClients()) {
      if (c == client || c == null) continue;
      PacketOtherPlayerConnected popc = new PacketOtherPlayerConnected();
      popc.username = c.getPlayer().username;
      popc.uuid = c.getPlayer().uuid;
      popc.position = c.getPlayer().position;
      popc.angle = c.getPlayer().angle;
      NetworkingManager.sendPacketToClient(popc, client);
    }

    clientIdentifier.getPlayer().addToWorld();

    initialLoadAreas();
  }

  private void initialLoadAreas() {
    AreaReference check = new AreaReference();
    synchronized (this) {
      for (int areaX = playerArea.areaX - loadDistance; areaX <= playerArea.areaX + loadDistance; areaX++) {
        check.areaX = areaX;
        for (int areaZ = playerArea.areaZ - loadDistance; areaZ <= playerArea.areaZ + loadDistance; areaZ++) {
          check.areaZ = areaZ;
          check.modified();
          Area area = server.world.getArea(check, false); //don't request individually, request in a batch
          if (area != null && area.features()) sendArea(area);
        }
      }
      WorldRequestParameter parameter = new WorldRequestParameter(playerArea.clone(), new Runnable() {
        @Override
        public void run() {
          NetworkingManager.sendPacketToClient(new PacketInitialAreasLoaded(), client);
        }
      });
      initialGenerationTask = server.world.requestRegion(new WorldRegion(playerArea, loadDistance), parameter);
    }
  }

  private void teleportToSpawn() {
    BlockReference spawn = server.world.spawnpoint;
    if (spawn.blockY < 0)
      throw new IllegalStateException("The spawn point y coordinate must be greater than 0. " + spawn.blockY + " < 0");
    setPosition(spawn.asVector3().add(0.5f, 3f, 0.5f), null, false);
  }

  public void handlePacket(PacketPlayerMovement packetPlayerMovement) {
    setPosition(packetPlayerMovement.position, packetPlayerMovement.angle, true);
  }

  public void setPosition(Vector3 newPosition, Vector3 newAngle, boolean clientKnows) {
    synchronized (this) {
      if (newPosition != null) {
        float x = newPosition.x, y = newPosition.y, z = newPosition.z;
        if (new PlayerMovementEvent(client.getPlayer(), newPosition).post().isCanceled()) {
          // cancel move
          if (!clientKnows) return;
          // client knows new position, need to send old position
          newPosition = client.getPlayer().position.cpy();
          newAngle = client.getPlayer().angle.cpy();
          clientKnows = false;
        } else if (newPosition.x != x || newPosition.y != y || newPosition.z != z) {
          // event handler changed new position
          clientKnows = false;
        }
      }

      if (newPosition == null) newPosition = client.getPlayer().position.cpy();
      if (newAngle == null) newAngle = client.getPlayer().angle.cpy();

      AreaReference newRef = new AreaReference().setFromPositionVector3(newPosition);
      AreaReference oldRef = new AreaReference().setFromPositionVector3(client.getPlayer().position);
      if (!newRef.equals(oldRef)) {
        WorldRegion newRegion = new WorldRegion(newRef, loadDistance);
        WorldRegion oldRegion = new WorldRegion(oldRef, loadDistance);
        AreaReferenceSet difference = new AreaReferenceSet();
        difference.addAll(newRegion.getAreaReferences());
        difference.removeAll(oldRegion.getAreaReferences());

        for (AreaReference areaReference : difference) {
          Area area = server.world.getArea(areaReference, false); //don't request individually, request in a batch
          if (area != null && area.features()) sendArea(area);
        }

        server.world.requestRegion(difference, null);
        playerArea.setFromAreaReference(newRef);
      }

      client.getPlayer().position.set(newPosition);
      client.getPlayer().angle.set(newAngle);

      if (client.getPlayer().position.y < -10f) teleportToSpawn();

      if (!clientKnows) NetworkingManager.sendPacketToClient(new PacketPlayerMovement(client.getPlayer()), client);
      NetworkingManager.sendPacketToOtherClients(new PacketOtherPlayerMovement(client.getPlayer()), client);
    }
  }

  @EventHandler
  public void blockChanged(BlockChangedEvent event) {
    BlockReference blockReference = event.getBlockReference();
    synchronized (this) {
      if (Math.abs(CoordinateConverter.area(blockReference.blockX) - playerArea.areaX) > loadDistance) return;
      if (Math.abs(CoordinateConverter.area(blockReference.blockZ) - playerArea.areaZ) > loadDistance) return;
    }
    if (Area.isShared()) {
      PacketAreaUpdateRender packet = new PacketAreaUpdateRender();
      packet.areaX = CoordinateConverter.area(blockReference.blockX);
      packet.areaZ = CoordinateConverter.area(blockReference.blockZ);
      packet.ySection = CoordinateConverter.area(blockReference.blockY);
      NetworkingManager.sendPacketToClient(packet, client);
    } else {
      PacketBlockChanged packet = new PacketBlockChanged();
      packet.x = blockReference.blockX;
      packet.y = blockReference.blockY;
      packet.z = blockReference.blockZ;
      packet.block = Sided.getIDManager().toInt(event.getNewBlock());
      NetworkingManager.sendPacketToClient(packet, client);
    }
  }

  @EventHandler
  public void areaLoaded(AreaLoadedEvent event) {
    Area area = event.getArea();
    synchronized (this) {
      if (Math.abs(area.areaX - playerArea.areaX) > loadDistance) return;
      if (Math.abs(area.areaZ - playerArea.areaZ) > loadDistance) return;
    }
    sendArea(area);
  }

  public boolean shouldSendArea(int areaX, int areaZ) {
    synchronized (this) {
      return !(Math.abs(areaX - playerArea.areaX) > loadDistance || Math.abs(areaZ - playerArea.areaZ) > loadDistance);
    }
  }

  public void sendArea(Area area) {
    synchronized (this) {
      PacketArea packet = new PacketArea();
      packet.area = area;
      packet.playerManager = this;
      NetworkingManager.sendPacketToClient(packet, client);
    }
  }

  public void handlePacket(PacketButton packetButton) {
    synchronized (buttons) {
      switch (packetButton.action) {
        case PacketButton.BUTTON_DOWN:
          if (!buttons.contains(packetButton.button)) buttons.add(packetButton.button);
          if (!recentButtons.contains(packetButton.button)) recentButtons.add(packetButton.button);
          return;
        case PacketButton.BUTTON_UP:
          buttons.remove((Integer) packetButton.button);
          return;
      }
    }
  }

  public void handlePacket(PacketKey packetKey) {
    synchronized (keys) {
      switch (packetKey.action) {
        case PacketKey.KEY_DOWN:
          if (!keys.contains(packetKey.key)) buttons.add(packetKey.key);
          if (!recentKeys.contains(packetKey.key)) recentKeys.add(packetKey.key);
          return;
        case PacketKey.KEY_UP:
          keys.remove((Integer) packetKey.key);
          return;
      }
    }
  }

  public boolean keyDown(int key) {
    synchronized (keys) {
      return keys.contains(key);
    }
  }

  public boolean keyDownRecent(int key) {
    synchronized (keys) {
      return recentKeys.contains(key);
    }
  }

  public boolean keyUp(int key) {
    synchronized (keys) {
      return !keys.contains(key);
    }
  }

  public boolean buttonDown(int button) {
    synchronized (buttons) {
      return buttons.contains(button);
    }
  }

  public boolean buttonUp(int button) {
    synchronized (buttons) {
      return !buttons.contains(button);
    }
  }

  protected void update() {
    ItemTool.mine(client.getPlayer(), buttonDown(Buttons.LEFT));
    if (keyDownRecent(Keys.Q)) {
      PlayerInventory inventory = client.getPlayer().getInventory();
      ItemStack itemStack = InventoryHelper.reduceCount(inventory, inventory.hotbarSelected);
      if (itemStack != null) {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.itemStack = itemStack;
        itemEntity.position.set(client.getPlayer().position);
        itemEntity.motion.set(client.getPlayer().angle);
        itemEntity.age = -3000 / Cubes.tickMS;
        Cubes.getServer().world.addEntity(itemEntity);
      }
    }
    synchronized (buttons) {
      for (Integer recentButton : recentButtons) {
        ItemStack itemStack = client.getPlayer().getInventory().selectedItemStack();
        if (itemStack != null)
          itemStack.item.onButtonPress(recentButton, itemStack, client.getPlayer(), client.getPlayer().getInventory().hotbarSelected);
      }

      recentButtons.clear();
    }
    synchronized (keys) {
      recentKeys.clear();
    }
    synchronized (this) {
      if (initialGenerationTask != null) {
        int doneGenerate = initialGenerationTask.doneGenerate();
        int doneFeatures = initialGenerationTask.doneFeatures();
        int totalGenerate = initialGenerationTask.totalGenerate();
        int totalFeatures = initialGenerationTask.totalFeatures();

        PacketInitialAreasProgress packet = new PacketInitialAreasProgress();
        packet.progress = ((float) (doneGenerate + doneFeatures)) / ((float) (totalGenerate + totalGenerate));
        NetworkingManager.sendPacketToClient(packet, client);

        if (doneFeatures == totalFeatures && doneGenerate == totalGenerate) initialGenerationTask = null;
      }
    }
  }

  public boolean buttonDownRecent(int button) {
    synchronized (buttons) {
      return recentButtons.contains(button);
    }
  }

  public void disconnected() {
    Cubes.getServer().world.save.writePlayer(client.getPlayer());
    Cubes.getServer().world.removeEntity(client.getPlayer().uuid);

    PacketChat packetChat = new PacketChat();
    packetChat.msg = client.getPlayer().username + " disconnected";
    NetworkingManager.sendPacketToAllClients(packetChat);
  }
}
