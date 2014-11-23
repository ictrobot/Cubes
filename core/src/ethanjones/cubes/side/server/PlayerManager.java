package ethanjones.cubes.side.server;

import com.badlogic.gdx.Input.Buttons;
import java.util.ArrayList;

import ethanjones.cubes.core.event.EventHandler;
import ethanjones.cubes.core.event.world.block.BlockEvent;
import ethanjones.cubes.core.system.Executor;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.graphics.world.RayTracing;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packets.*;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.WorldServer;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.reference.BlockReference;
import ethanjones.cubes.world.storage.Area;
import ethanjones.cubes.world.storage.BlankArea;
import ethanjones.cubes.world.thread.GenerateWorldCallable;
import ethanjones.cubes.world.thread.SendWorldCallable;

public class PlayerManager {

  public final ClientIdentifier client;
  private final CubesServer server;
  private final AreaReference playerArea;
  private final ArrayList<Integer> keys;
  private final ArrayList<Integer> buttons;
  private int renderDistance;

  public PlayerManager(ClientIdentifier clientIdentifier, PacketConnect packetConnect) {
    this.server = Cubes.getServer();
    this.client = clientIdentifier;
    this.playerArea = new AreaReference().setFromPositionVector3(client.getPlayer().position);
    this.keys = new ArrayList<Integer>();
    this.buttons = new ArrayList<Integer>();

    renderDistance = packetConnect.renderDistance;

    Sided.getEventBus().register(this);

    NetworkingManager.sendPacketToClient(new PacketConnected(), clientIdentifier);

    initialLoadAreas();
  }

  private void initialLoadAreas() {
    AreaReference check = new AreaReference();
    synchronized (playerArea) {
      for (int areaX = playerArea.areaX - renderDistance; areaX <= playerArea.areaX + renderDistance; areaX++) {
        check.areaX = areaX;
        for (int areaY = Math.max(playerArea.areaY - renderDistance, 0); areaY <= playerArea.areaY + renderDistance; areaY++) {
          check.areaY = areaY;
          for (int areaZ = playerArea.areaZ - renderDistance; areaZ <= playerArea.areaZ + renderDistance; areaZ++) {
            check.areaZ = areaZ;
            sendAndRequestArea(check);
          }
        }
      }
    }
  }

  private void sendAndRequestArea(AreaReference areaReference) {
    Area area = server.world.getAreaInternal(areaReference, false, false);
    if (area == null || area instanceof BlankArea) {
      Executor.execute(new SendWorldCallable(new GenerateWorldCallable(areaReference.clone(), (WorldServer) server.world), client, this));
    } else {
      Executor.execute(new SendWorldCallable(server.world.getAreaInternal(areaReference, false, false), client, this));
    }
  }

  public void handlePacket(PacketPlayerInfo packetPlayerInfo) {
    AreaReference n = new AreaReference().setFromPositionVector3(packetPlayerInfo.position);
    AreaReference o = new AreaReference().setFromPositionVector3(client.getPlayer().position);
    if (!n.equals(o)) {
      AreaReference check = new AreaReference();
      for (int areaX = n.areaX - renderDistance; areaX <= n.areaX + renderDistance; areaX++) {
        check.areaX = areaX;
        for (int areaY = Math.max(n.areaY - renderDistance, 0); areaY <= n.areaY + renderDistance; areaY++) {
          check.areaY = areaY;
          for (int areaZ = n.areaZ - renderDistance; areaZ <= n.areaZ + renderDistance; areaZ++) {
            check.areaZ = areaZ;
            if (Math.abs(areaX - o.areaX) > renderDistance || Math.abs(areaY - o.areaY) > renderDistance || Math.abs(areaZ - o.areaZ) > renderDistance) {
              sendAndRequestArea(check);
            }
          }
        }
      }
    }
    if (!client.getPlayer().position.equals(packetPlayerInfo.position)) {
      client.getPlayer().position.set(packetPlayerInfo.position);
      synchronized (playerArea) {
        playerArea.setFromPositionVector3(client.getPlayer().position);
      }
    }
    client.getPlayer().angle.set(packetPlayerInfo.angle);
  }

  @EventHandler
  public void blockChanged(BlockEvent blockEvent) {
    BlockReference blockReference = blockEvent.getBlockReference();
    synchronized (playerArea) {
      if (Math.abs(CoordinateConverter.area(blockReference.blockX) - playerArea.areaX) > renderDistance) return;
      if (Math.abs(CoordinateConverter.area(blockReference.blockY) - playerArea.areaY) > renderDistance) return;
      if (Math.abs(CoordinateConverter.area(blockReference.blockZ) - playerArea.areaZ) > renderDistance) return;
    }
    PacketBlockChanged packet = new PacketBlockChanged();
    packet.x = blockReference.blockX;
    packet.y = blockReference.blockY;
    packet.z = blockReference.blockZ;
    packet.block = Sided.getBlockManager().toInt(server.world.getBlock(packet.x, packet.y, packet.z));
    NetworkingManager.sendPacketToClient(packet, client);
  }

  public boolean shouldSendArea(int areaX, int areaY, int areaZ) {
    synchronized (playerArea) {
      if (Math.abs(areaX - playerArea.areaX) <= renderDistance && Math.abs(areaY - playerArea.areaY) <= renderDistance && Math.abs(areaZ - playerArea.areaZ) <= renderDistance) {
        return true;
      }
      return false;
    }
  }

  public void handlePacket(PacketButton packetButton) {
    synchronized (buttons) {
      switch (packetButton.action) {
        case PacketButton.BUTTON_DOWN:
          if (!buttons.contains(packetButton.button)) buttons.add(packetButton.button);
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
    if (buttonDown(Buttons.LEFT)) {
      RayTracing.BlockIntersection blockIntersection = RayTracing.getBlockIntersection(client.getPlayer().position, client.getPlayer().angle, server.world);
      if (blockIntersection == null) return;
      BlockReference blockReference = blockIntersection.getBlockReference();
      server.world.setBlock(null, blockReference.blockX, blockReference.blockY, blockReference.blockZ);
    }
  }
}
