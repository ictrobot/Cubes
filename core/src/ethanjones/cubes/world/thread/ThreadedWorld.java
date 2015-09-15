package ethanjones.cubes.world.thread;

import ethanjones.cubes.core.event.world.generation.FeaturesEvent;
import ethanjones.cubes.core.event.world.generation.GenerationEvent;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packets.PacketArea;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.side.server.PlayerManager;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.server.WorldServer;
import ethanjones.cubes.world.storage.Area;

import java.util.concurrent.Callable;

public class ThreadedWorld {
  public static Area generate(AreaReference areaReference, WorldServer world) {
    Area area = world.getAreaInternal(areaReference, false);
    if (area != null) return area;

    area = new Area(areaReference.areaX, areaReference.areaZ);

    world.getTerrainGenerator().generate(area);
    new GenerationEvent(area, areaReference).post();
    area.updateAll();
    world.setAreaInternal(areaReference, area);

    return area;
  }

  public static Area features(AreaReference areaReference, WorldServer world) {
    Area area = world.getArea(areaReference);
    if (area == null) area = generate(areaReference, world);
    if (area.features()) return area;

    world.getTerrainGenerator().features(area, world);
    new FeaturesEvent(area, areaReference).post();
    area.setFeatures();
    area.updateAll();

    return area;
  }

  public static Area send(AreaReference areaReference, WorldServer world, ClientIdentifier clientIdentifier, PlayerManager playerManager) {
    Area area = world.getArea(areaReference);
    if (area == null || !area.features()) area = features(areaReference, world);
    PacketArea packetArea = new PacketArea();
    packetArea.area = area;
    packetArea.playerManager = playerManager;
    NetworkingManager.sendPacketToClient(packetArea, clientIdentifier);
    return area;
  }

  public static class GenerateCallable implements Callable<Area> {

    private final AreaReference areaReference;
    private final WorldServer world;

    public GenerateCallable(AreaReference areaReference, WorldServer world) {
      this.areaReference = areaReference;
      this.world = world;
    }

    @Override
    public Area call() throws Exception {
      return generate(areaReference, world);
    }
  }

  public static class FeaturesCallable implements Callable<Area> {

    private final AreaReference areaReference;
    private final WorldServer world;

    public FeaturesCallable(AreaReference areaReference, WorldServer world) {
      this.areaReference = areaReference;
      this.world = world;
    }

    @Override
    public Area call() throws Exception {
      return features(areaReference, world);
    }
  }

  public static class SendCallable implements Callable {

    private final AreaReference areaReference;
    private final WorldServer worldServer;
    private final ClientIdentifier clientIdentifier;
    private final PlayerManager playerManager;

    public SendCallable(AreaReference areaReference, WorldServer worldServer, ClientIdentifier clientIdentifier, PlayerManager playerManager) {
      this.areaReference = areaReference;
      this.worldServer = worldServer;
      this.clientIdentifier = clientIdentifier;
      this.playerManager = playerManager;
    }

    @Override
    public Object call() throws Exception {
      send(areaReference, worldServer, clientIdentifier, playerManager);
      return null;
    }
  }
}
