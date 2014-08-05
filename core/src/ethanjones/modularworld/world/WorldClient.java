package ethanjones.modularworld.world;

import ethanjones.modularworld.networking.NetworkingManager;
import ethanjones.modularworld.networking.packets.PacketRequestWorld;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.world.reference.AreaReference;
import ethanjones.modularworld.world.storage.Area;

public class WorldClient extends World {

  public final static int AREA_LOAD_DISTANCE = (AREA_LOAD_RADIUS * 2) + 1;
  public final static int AREA_LOAD_DISTANCE_SQUARED = AREA_LOAD_DISTANCE * AREA_LOAD_DISTANCE;
  public final static int AREA_LOAD_DISTANCE_CUBED = AREA_LOAD_DISTANCE_SQUARED * AREA_LOAD_DISTANCE;

  public Area[] areasAroundPlayer;
  public int minAreaX;
  public int minAreaY;
  public int minAreaZ;
  public AreaReference playerArea;

  public WorldClient() {
    super();
    playerArea = new AreaReference().setFromVector3(ModularWorldClient.instance.player.position);
    minAreaX = playerArea.areaX - AREA_LOAD_RADIUS;
    minAreaY = playerArea.areaY - AREA_LOAD_RADIUS;
    minAreaZ = playerArea.areaZ - AREA_LOAD_RADIUS;
    areasAroundPlayer = new Area[AREA_LOAD_DISTANCE_CUBED];
  }

  public int getArrayPos(int arrayX, int arrayY, int arrayZ) {
    return arrayX + arrayZ * AREA_LOAD_DISTANCE + arrayY * AREA_LOAD_DISTANCE_SQUARED;
  }


  public void playerChangedPosition() {
    playerArea.setFromVector3(ModularWorldClient.instance.player.position);
    if (playerArea.areaX - AREA_LOAD_RADIUS != minAreaX || playerArea.areaY - AREA_LOAD_RADIUS != minAreaY || playerArea.areaZ - AREA_LOAD_RADIUS != minAreaZ) {
      minAreaX = playerArea.areaX - AREA_LOAD_RADIUS;
      minAreaY = playerArea.areaY - AREA_LOAD_RADIUS;
      minAreaZ = playerArea.areaZ - AREA_LOAD_RADIUS;
      Area[] old = areasAroundPlayer;
      areasAroundPlayer = new Area[AREA_LOAD_DISTANCE_CUBED];
      AreaReference areaReference = areaReferencePool.obtain();
      for (int x = 0; x < AREA_LOAD_DISTANCE; x++) {
        for (int y = 0; y < AREA_LOAD_DISTANCE; y++) {
          for (int z = 0; z < AREA_LOAD_DISTANCE; z++) {
            Area o = old[getArrayPos(x, y, z)];
            if (o != null) {
              areaReference.setFromArea(o);
              if (!setAreaInternal(areaReference, o)) {
                o.unload();
              }
            }
          }
        }
      }
      areaReferencePool.free(areaReference);
    }
  }

  protected Area getAreaInternal(AreaReference areaReference, boolean request, boolean generatedCheck) {
    updateArrayPositions(areaReference);
    if (isArrayPositionValid(areaReference)) {
      Area area = areasAroundPlayer[areaReference.arrayPos];
      if (area != null && (area.generated || !generatedCheck)) {
        return area;
      } else if (area == null && request) {
        requestArea(areaReference);
      }
    }
    return BLANK_AREA;
  }

  public boolean setAreaInternal(AreaReference areaReference, Area area) {
    updateArrayPositions(areaReference);
    if (isArrayPositionValid(areaReference)) {
      areasAroundPlayer[areaReference.arrayPos] = area;
      return true;
    }
    return false;
  }

  public Area getAreaPlain(AreaReference areaReference) {
    return getAreaInternal(areaReference, false, false);
  }

  public Area getArea(AreaReference areaReference) {
    return getAreaInternal(areaReference, true, true);
  }

  public Area getArea(int areaX, int areaY, int areaZ) {
    AreaReference areaReference = areaReferencePool.obtain().setFromArea(areaX, areaY, areaZ);
    Area area = getArea(areaReference);
    areaReferencePool.free(areaReference);
    return area;
  }

  private AreaReference updateArrayPositions(AreaReference areaReference) {
    areaReference.arrayX = areaReference.areaX - playerArea.areaX + AREA_LOAD_RADIUS;
    areaReference.arrayY = areaReference.areaY - playerArea.areaY + AREA_LOAD_RADIUS;
    areaReference.arrayY = areaReference.areaZ - playerArea.areaZ + AREA_LOAD_RADIUS;
    areaReference.arrayPos = getArrayPos(areaReference.arrayX, areaReference.arrayY, areaReference.arrayZ);
    return areaReference;
  }

  private boolean isArrayPositionValid(AreaReference areaReference) {
    return !(areaReference.arrayX < 0 || areaReference.arrayX > AREA_LOAD_DISTANCE || areaReference.arrayY < 0 || areaReference.arrayY > AREA_LOAD_DISTANCE || areaReference.arrayZ < 0 || areaReference.arrayZ > AREA_LOAD_DISTANCE || areaReference.arrayPos >= areasAroundPlayer.length || areaReference.arrayPos <= 0);
  }

  public void requestArea(AreaReference areaReference) {
    request(areaReference);
  }

  public void request(AreaReference areaReference) {
    PacketRequestWorld packetRequestWorld = new PacketRequestWorld();
    packetRequestWorld.areaX = areaReference.areaX;
    packetRequestWorld.areaY = areaReference.areaY;
    packetRequestWorld.areaZ = areaReference.areaZ;
    NetworkingManager.clientNetworking.sendToServer(packetRequestWorld);
  }

  @Override
  public void dispose() {

  }
}
