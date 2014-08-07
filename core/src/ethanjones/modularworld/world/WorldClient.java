package ethanjones.modularworld.world;

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
      Area[] old;
      synchronized (this) {
        old = areasAroundPlayer;
        areasAroundPlayer = new Area[AREA_LOAD_DISTANCE_CUBED];
      }
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


  protected Area getAreaInternal(AreaReference areaReference, boolean request) {
    updateArrayPositions(areaReference);
    if (isArrayPositionValid(areaReference)) {
      Area area;
      synchronized (areasAroundPlayer) {
        area = areasAroundPlayer[areaReference.arrayPos];
      }
      if (area != null) {
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
      synchronized (areasAroundPlayer) {
        areasAroundPlayer[areaReference.arrayPos] = area;
      }
      return true;
    }
    return false;
  }

  private AreaReference updateArrayPositions(AreaReference areaReference) {
    areaReference.arrayX = areaReference.areaX - playerArea.areaX + AREA_LOAD_RADIUS;
    areaReference.arrayY = areaReference.areaY - playerArea.areaY + AREA_LOAD_RADIUS;
    areaReference.arrayY = areaReference.areaZ - playerArea.areaZ + AREA_LOAD_RADIUS;
    areaReference.arrayPos = getArrayPos(areaReference.arrayX, areaReference.arrayY, areaReference.arrayZ);
    return areaReference;
  }

  private boolean isArrayPositionValid(AreaReference areaReference) {
    return !(areaReference.arrayX < 0 || areaReference.arrayX > AREA_LOAD_DISTANCE || areaReference.arrayY < 0 || areaReference.arrayY > AREA_LOAD_DISTANCE || areaReference.arrayZ < 0 || areaReference.arrayZ > AREA_LOAD_DISTANCE || areaReference.arrayPos >= AREA_LOAD_DISTANCE_CUBED || areaReference.arrayPos <= 0);
  }

  public void requestArea(AreaReference areaReference) {
    request(areaReference);
  }

  public void request(AreaReference areaReference) {
  }

  @Override
  public void dispose() {

  }
}
