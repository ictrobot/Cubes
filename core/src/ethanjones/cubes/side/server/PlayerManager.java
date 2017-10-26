package ethanjones.cubes.side.server;

import ethanjones.cubes.core.event.EventHandler;
import ethanjones.cubes.core.event.entity.living.player.PlayerMovementEvent;
import ethanjones.cubes.core.event.world.block.BlockChangedEvent;
import ethanjones.cubes.core.event.world.generation.AreaLoadedEvent;
import ethanjones.cubes.core.id.IDManager;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.entity.Entity;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.input.ClickType;
import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.ItemTool;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.client.ClientNetworking;
import ethanjones.cubes.networking.packets.*;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.common.Side;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.collision.BlockIntersection;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.reference.BlockReference;
import ethanjones.cubes.world.reference.multi.AreaReferenceSet;
import ethanjones.cubes.world.reference.multi.WorldRegion;
import ethanjones.cubes.world.server.WorldServer;
import ethanjones.cubes.world.storage.Area;
import ethanjones.cubes.world.thread.GenerationTask;
import ethanjones.cubes.world.thread.WorldRequestParameter;

import com.badlogic.gdx.math.Vector3;

public class PlayerManager {
  
  public final ClientIdentifier client;
  public double connectionPing = -1;
  public double lastPingNano = -1;
  private final CubesServer server;
  private final AreaReference playerArea;
  private GenerationTask initialGenerationTask;
  private int renderDistance;
  private int loadDistance;
  public ClickType clickType;
  
  public PlayerManager(ClientIdentifier clientIdentifier, PacketConnect packetConnect) {
    this.server = Cubes.getServer();
    this.client = clientIdentifier;
    this.playerArea = new AreaReference().setFromPositionVector3(client.getPlayer().position);
    
    renderDistance = packetConnect.renderDistance;
    loadDistance = renderDistance + 1;
    
    Side.getSidedEventBus().register(this);
    
    PacketConnected packetConnected = new PacketConnected();
    packetConnected.idManager = IDManager.writeMapping();
    packetConnected.player = client.getPlayer().uuid;
    packetConnected.worldTime = server.world.getTime();
    packetConnected.gamemode = server.world.save.getSaveOptions().worldGamemode;
    NetworkingManager.sendPacketToClient(packetConnected, client);
    
    PacketPlayerInventory packetPlayerInventory = new PacketPlayerInventory();
    packetPlayerInventory.inv = clientIdentifier.getPlayer().getInventory().write();
    NetworkingManager.sendPacketToClient(packetPlayerInventory, client);
    
    if (clientIdentifier.getPlayer().position.isZero()) teleportToSpawn();
    else setPosition(null, null, false);
    
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
          if (area != null && area.featuresGenerated()) sendArea(area);
        }
      }
      WorldRequestParameter parameter = new WorldRequestParameter(playerArea.copy(), new Runnable() {
        @Override
        public void run() {
          NetworkingManager.sendPacketToClient(new PacketInitialAreasLoaded(), client);
        }
      });
      initialGenerationTask = server.world.requestRegion(new WorldRegion(playerArea, loadDistance), parameter);
      
      for (Entity entity : Cubes.getServer().world.entities.values()) {
        if (positionInLoadRange(entity.position) && !(entity instanceof Player)) {
          PacketEntityAdd packet = new PacketEntityAdd();
          packet.entity = entity;
          NetworkingManager.sendPacketToClient(packet, client);
        }
      }
    }
  }
  
  private void teleportToSpawn() {
    BlockReference spawn = ((WorldServer) server.world).getSpawnPoint();
    if (spawn.blockY < 0)
      throw new IllegalStateException("The spawn point y coordinate must be greater than 0. " + spawn.blockY + " < 0");
    setPosition(spawn.asVector3().add(0.5f, Player.PLAYER_HEIGHT, 0.5f), null, false);
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
          if (area != null && area.featuresGenerated()) sendArea(area);
        }
        
        server.world.requestRegion(difference, null);
        
        for (Entity entity : Cubes.getServer().world.entities.values()) {
          if (!(entity instanceof Player) && newRegion.contains(entity.position) && !oldRegion.contains(entity.position)) {
            PacketEntityAdd packet = new PacketEntityAdd();
            packet.entity = entity;
            NetworkingManager.sendPacketToClient(packet, client);
          }
        }
        
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
      packet.meta = event.getNewMeta();
      packet.block = IDManager.toInt(event.getNewBlock());
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
  
  protected void update() {
    synchronized (this) {
      if (clickType != ClickType.none) {
        ItemTool.mine(client.getPlayer(), clickType == ClickType.mine);
        
        ItemStack itemStack = client.getPlayer().getInventory().selectedItemStack();
        boolean b = true;
        BlockIntersection blockIntersection = BlockIntersection.getBlockIntersection(client.getPlayer().position, client.getPlayer().angle, server.world);
        if (blockIntersection != null) {
          BlockReference r = blockIntersection.getBlockReference();
          b = !server.world.getBlock(r.blockX, r.blockY, r.blockZ).onButtonPress(clickType, client.getPlayer(), r.blockX, r.blockY, r.blockZ);
        }
        if (b && itemStack != null) {
          b = !itemStack.item.onButtonPress(clickType, itemStack, client.getPlayer(), client.getPlayer().getInventory().hotbarSelected);
        }
        
        if (clickType == ClickType.place) clickType = ClickType.none;
      }
      
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
      
      if (lastPingNano != -1 && System.nanoTime() - lastPingNano >= ClientNetworking.PING_NANOSECONDS * 2) {
        NetworkingManager.getNetworking(Side.Server).disconnected(client.getSocketMonitor(), new CubesException("No ping received"));
      }
    }
  }
  
  public void disconnected() {
    Cubes.getServer().world.save.writePlayer(client.getPlayer());
    Cubes.getServer().world.removeEntity(client.getPlayer().uuid);
    ((WorldServer) Cubes.getServer().world).removeLoadedAreaFilter(client.getPlayer());

    PacketChat packetChat = new PacketChat();
    packetChat.msg = client.getPlayer().username + " disconnected";
    NetworkingManager.sendPacketToAllClients(packetChat);
  }
  
  public boolean positionInLoadRange(Vector3 position) {
    int areaX = CoordinateConverter.area(position.x);
    int areaZ = CoordinateConverter.area(position.z);
    return areaX >= (playerArea.areaX - loadDistance) && areaX <= (playerArea.areaX + loadDistance) && areaZ >= (playerArea.areaZ - loadDistance) && areaZ <= (playerArea.areaZ + loadDistance);
  }

  public boolean areaInLoadRange(AreaReference a) {
    return a.areaX >= (playerArea.areaX - loadDistance) && a.areaX <= (playerArea.areaX + loadDistance) && a.areaZ >= (playerArea.areaZ - loadDistance) && a.areaZ <= (playerArea.areaZ + loadDistance);
  }
}
