package ethanjones.modularworld.world;

import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.world.reference.AreaReference;
import ethanjones.modularworld.world.storage.Area;

public class WorldClient extends World {

  public final static int AREA_LOAD_DISTANCE = (AREA_LOAD_RADIUS * 2) + 1;
  public final static int AREA_LOAD_DISTANCE_SQUARED = AREA_LOAD_DISTANCE * AREA_LOAD_DISTANCE;
  public final static int AREA_LOAD_DISTANCE_CUBED = AREA_LOAD_DISTANCE_SQUARED * AREA_LOAD_DISTANCE;

  public Area[] areasAroundPlayer;
  public volatile int minAreaX;
  public volatile int minAreaY;
  public volatile int minAreaZ;
  public volatile AreaReference playerArea;

  public WorldClient() {
    super();
    playerArea = new AreaReference().setFromVector3(ModularWorldClient.instance.player.position);
    minAreaX = playerArea.areaX - AREA_LOAD_RADIUS;
    minAreaY = playerArea.areaY - AREA_LOAD_RADIUS;
    minAreaZ = playerArea.areaZ - AREA_LOAD_RADIUS;
    areasAroundPlayer = new Area[AREA_LOAD_DISTANCE_CUBED];
  }


  public void playerChangedPosition() {
    synchronized (this) {
      playerArea.setFromVector3(ModularWorldClient.instance.player.position);
      if (playerArea.areaX - AREA_LOAD_RADIUS != minAreaX || playerArea.areaY - AREA_LOAD_RADIUS != minAreaY || playerArea.areaZ - AREA_LOAD_RADIUS != minAreaZ) {
        minAreaX = playerArea.areaX - AREA_LOAD_RADIUS;
        minAreaY = playerArea.areaY - AREA_LOAD_RADIUS;
        minAreaZ = playerArea.areaZ - AREA_LOAD_RADIUS;
        Area[] old;
        old = areasAroundPlayer;
        areasAroundPlayer = new Area[AREA_LOAD_DISTANCE_CUBED];
        AreaReference areaReference = areaReferencePool.obtain();
        for (int i = 0; i < AREA_LOAD_DISTANCE_CUBED; i++) {
          Area o = old[i];
          if (o != null) {
            areaReference.setFromArea(o);
            if (!setAreaInternal(areaReference, o)) {
              o.unload();
            }
          }
        }
        areaReferencePool.free(areaReference);
      }
    }
  }


  public Area getAreaInternal(AreaReference areaReference, boolean request, boolean returnBlank) {
    int arrayPos = getArrayPos(areaReference);
    if (isArrayPositionValid(arrayPos)) {
      Area area;
      synchronized (this) {
        area = areasAroundPlayer[arrayPos];
      }
      if (area != null) {
        return area;
      } else if (request) {
        requestArea(areaReference);
      }
    }
    return returnBlank ? BLANK_AREA : null;
  }

  public boolean setAreaInternal(AreaReference areaReference, Area area) {
    int arrayPos = getArrayPos(areaReference);
    if (isArrayPositionValid(arrayPos)) {
      synchronized (this) {
        areasAroundPlayer[arrayPos] = area;
      }
      return true;
    }
    return false;
  }

  private int getArrayPos(AreaReference areaReference) {
    synchronized (this) {
      int arrayX = areaReference.areaX - playerArea.areaX + AREA_LOAD_RADIUS;
      int arrayY = areaReference.areaY - playerArea.areaY + AREA_LOAD_RADIUS;
      int arrayZ = areaReference.areaZ - playerArea.areaZ + AREA_LOAD_RADIUS;
      return arrayX + arrayZ * AREA_LOAD_DISTANCE + arrayY * AREA_LOAD_DISTANCE_SQUARED;
    }
  }

  private boolean isArrayPositionValid(int arrayPos) {
    return arrayPos < AREA_LOAD_DISTANCE_CUBED && arrayPos >= 0;
  }

  public void requestArea(AreaReference areaReference) {

  }

  @Override
  public void dispose() {
    areasAroundPlayer = null;
  }
}
