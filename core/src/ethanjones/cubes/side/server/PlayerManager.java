package ethanjones.cubes.side.server;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.event.EventHandler;
import ethanjones.cubes.core.event.world.block.BlockChangedEvent;
import ethanjones.cubes.core.event.world.generation.AreaGeneratedEvent;
import ethanjones.cubes.graphics.world.RayTracing;
import ethanjones.cubes.item.ItemStack;
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

import com.badlogic.gdx.Input.Buttons;

import java.util.ArrayList;

public class PlayerManager {

  public final ClientIdentifier client;
  private final CubesServer server;
  private final AreaReference playerArea;
  private final ArrayList<Integer> keys;
  private final ArrayList<Integer> buttons;
  private final ArrayList<Integer> recentKeys;
  private final ArrayList<Integer> recentButtons;
  private int renderDistance;

  public PlayerManager(ClientIdentifier clientIdentifier, PacketConnect packetConnect) {
    this.server = Cubes.getServer();
    this.client = clientIdentifier;
    this.playerArea = new AreaReference().setFromPositionVector3(client.getPlayer().position);
    this.keys = new ArrayList<Integer>();
    this.buttons = new ArrayList<Integer>();
    this.recentKeys = new ArrayList<Integer>();
    this.recentButtons = new ArrayList<Integer>();

    renderDistance = packetConnect.renderDistance;

    Sided.getEventBus().register(this);

    PacketConnected packetConnected = new PacketConnected();
    packetConnected.idManager = Sided.getIDManager().write();
    NetworkingManager.sendPacketToClient(packetConnected, client);

    BlockReference spawn = server.world.spawnpoint;
    clientIdentifier.getPlayer().position.set(spawn.blockX, spawn.blockY + 2f, spawn.blockZ);
    NetworkingManager.sendPacketToClient(new PacketPlayerInfo(clientIdentifier.getPlayer()), client);

    initialLoadAreas();
  }

  private void initialLoadAreas() {
    AreaReference check = new AreaReference();
    synchronized (this) {
      for (int areaX = playerArea.areaX - renderDistance; areaX <= playerArea.areaX + renderDistance; areaX++) {
        check.areaX = areaX;
        for (int areaZ = playerArea.areaZ - renderDistance; areaZ <= playerArea.areaZ + renderDistance; areaZ++) {
          check.areaZ = areaZ;
          check.modified();
          Area area = server.world.getArea(check, false); //don't request individually, request in a batch
          if (area != null) sendArea(area);
        }
      }
      server.world.requestRegion(new WorldRegion(playerArea, renderDistance));
    }
  }

  public void handlePacket(PacketPlayerInfo packetPlayerInfo) {
    synchronized (this) {
      AreaReference newRef = new AreaReference().setFromPositionVector3(packetPlayerInfo.position);
      AreaReference oldRef = new AreaReference().setFromPositionVector3(client.getPlayer().position);
      if (!newRef.equals(oldRef)) {
        WorldRegion newRegion = new WorldRegion(newRef, renderDistance);
        WorldRegion oldRegion = new WorldRegion(oldRef, renderDistance);
        AreaReferenceSet difference = new AreaReferenceSet();
        difference.addAll(newRegion.getAreaReferences());
        difference.removeAll(oldRegion.getAreaReferences());

        for (AreaReference areaReference : difference) {
          Area area = server.world.getArea(areaReference, false); //don't request individually, request in a batch
          if (area != null) sendArea(area);
        }

        server.world.requestRegion(difference);
        playerArea.setFromAreaReference(newRef);
      }

      client.getPlayer().position.set(packetPlayerInfo.position);
      client.getPlayer().angle.set(packetPlayerInfo.angle);
    }
  }

  @EventHandler
  public void blockChanged(BlockChangedEvent event) {
    BlockReference blockReference = event.getBlockReference();
    synchronized (this) {
      if (Math.abs(CoordinateConverter.area(blockReference.blockX) - playerArea.areaX) > renderDistance) return;
      if (Math.abs(CoordinateConverter.area(blockReference.blockZ) - playerArea.areaZ) > renderDistance) return;
    }
    PacketBlockChanged packet = new PacketBlockChanged();
    packet.x = blockReference.blockX;
    packet.y = blockReference.blockY;
    packet.z = blockReference.blockZ;
    packet.block = Sided.getIDManager().toInt(event.getNewBlock());
    NetworkingManager.sendPacketToClient(packet, client);
  }

  @EventHandler
  public void areaSet(AreaGeneratedEvent event) {
    Area area = event.getArea();
    synchronized (this) {
      if (Math.abs(area.areaX - playerArea.areaX) > renderDistance) return;
      if (Math.abs(area.areaZ - playerArea.areaZ) > renderDistance) return;
    }
    sendArea(area);
  }

  public boolean shouldSendArea(int areaX, int areaZ) {
    synchronized (this) {
      return !(Math.abs(areaX - playerArea.areaX) > renderDistance || Math.abs(areaZ - playerArea.areaZ) > renderDistance);
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
    if (buttonDownRecent(Buttons.LEFT)) {
      RayTracing.BlockIntersection blockIntersection = RayTracing.getBlockIntersection(client.getPlayer().position, client.getPlayer().angle, server.world);
      if (blockIntersection != null) {
        BlockReference blockReference = blockIntersection.getBlockReference();
        server.world.setBlock(null, blockReference.blockX, blockReference.blockY, blockReference.blockZ);
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
  }

  public boolean buttonDownRecent(int button) {
    synchronized (buttons) {
      return recentButtons.contains(button);
    }
  }
}
