package ethanjones.modularworld.world;

import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.block.Block;
import ethanjones.modularworld.core.events.world.generation.GenerationEvent;
import ethanjones.modularworld.world.coordinates.AreaCoordinates;
import ethanjones.modularworld.world.coordinates.BlockCoordinates;
import ethanjones.modularworld.world.generator.WorldGenerator;
import ethanjones.modularworld.world.reference.AreaReference;
import ethanjones.modularworld.world.reference.AreaReferencePool;
import ethanjones.modularworld.world.storage.Area;
import ethanjones.modularworld.world.storage.BlankArea;
import ethanjones.modularworld.world.storage.Zone;

public class World {

  public final static int WORLD_RADIUS_ZONES = 1000;
  public final static int HEIGHT_LIMIT = Zone.SIZE_BLOCKS;
  public final static int AREA_LOAD_RADIUS = 10;
  public final static int AREA_LOAD_DISTANCE = (AREA_LOAD_RADIUS * 2) + 1;
  public Area[][][] areasAroundPlayer = new Area[AREA_LOAD_DISTANCE][AREA_LOAD_DISTANCE][AREA_LOAD_DISTANCE];
  public final static BlankArea BLANK_AREA = new BlankArea();
  public final WorldGenerator gen;
  public int minAreaX;
  public int minAreaY;
  public int minAreaZ;
  public AreaReference playerArea;
  private AreaReferencePool areaReferencePool;

  public World(WorldGenerator gen) {
    this.gen = gen;
    playerArea = new AreaReference().setFromVector3(ModularWorld.instance.player.position);
    minAreaX = playerArea.areaX - AREA_LOAD_RADIUS;
    minAreaY = playerArea.areaY - AREA_LOAD_RADIUS;
    minAreaZ = playerArea.areaZ - AREA_LOAD_RADIUS;
    areaReferencePool = new AreaReferencePool();
  }

  public void playerChangedPosition() {
    playerArea.setFromVector3(ModularWorld.instance.player.position);
    int newAreaX = playerArea.areaX - AREA_LOAD_RADIUS;
    int newAreaY = playerArea.areaY - AREA_LOAD_RADIUS;
    int newAreaZ = playerArea.areaZ - AREA_LOAD_RADIUS;
    if (newAreaX != minAreaX || newAreaY != minAreaY || newAreaZ != minAreaZ) {
      Area[][][] old = areasAroundPlayer;
      areasAroundPlayer = new Area[AREA_LOAD_DISTANCE][AREA_LOAD_DISTANCE][AREA_LOAD_DISTANCE];
      for (int x = 0; x < AREA_LOAD_DISTANCE; x++) {
        for (int y = 0; y < AREA_LOAD_DISTANCE; y++) {
          for (int z = 0; z < AREA_LOAD_DISTANCE; z++) {
            Area o = old[x][y][z];
            if (o != null) {
              int nX = o.x - playerArea.areaX;
              int nY = o.y - playerArea.areaY;
              int nZ = o.z - playerArea.areaZ;
              if (Math.abs(nX) > AREA_LOAD_RADIUS || Math.abs(nY) > AREA_LOAD_RADIUS || Math.abs(nZ) > AREA_LOAD_RADIUS) {
                o.unload();
              } else {
                nX += AREA_LOAD_RADIUS;
                nY += AREA_LOAD_RADIUS;
                nZ += AREA_LOAD_RADIUS;
                areasAroundPlayer[nX][nY][nZ] = o;
              }
            }
          }
        }
      }
    }
  }

  private Area getAreaInternal(AreaReference areaReference, boolean request, boolean generatedCheck) {
    updateArrayPositions(areaReference);
    if (isArrayPositionValid(areaReference)) {
      Area area = areasAroundPlayer[areaReference.arrayX][areaReference.arrayY][areaReference.arrayZ];
      if (area != null && (area.generated || !generatedCheck)) {
        return area;
      } else if (area == null && request) {
        requestArea(areaReference);
      } else if (area != null && !area.generated && request) {
        requestGeneration(areaReference);
      }
    }
    return BLANK_AREA;
  }

  public void setAreaInternal(AreaReference areaReference, Area area) {
    updateArrayPositions(areaReference);
    if (isArrayPositionValid(areaReference)) {
      areasAroundPlayer[areaReference.arrayX][areaReference.arrayY][areaReference.arrayZ] = area;
    }
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

  public Area getArea(AreaCoordinates areaCoordinates) {
    AreaReference areaReference = areaReferencePool.obtain().setFromAreaCoordinates(areaCoordinates);
    Area area = getArea(areaReference);
    areaReferencePool.free(areaReference);
    return area;
  }

  private AreaReference updateArrayPositions(AreaReference areaReference) {
    areaReference.arrayX = areaReference.areaX - playerArea.areaX + AREA_LOAD_RADIUS;
    areaReference.arrayY = areaReference.areaY - playerArea.areaY + AREA_LOAD_RADIUS;
    areaReference.arrayY = areaReference.areaZ - playerArea.areaZ + AREA_LOAD_RADIUS;
    return areaReference;
  }

  private boolean isArrayPositionValid(AreaReference areaReference) {
    return !(areaReference.arrayX < 0 || areaReference.arrayX > AREA_LOAD_DISTANCE || areaReference.arrayY < 0 || areaReference.arrayY > AREA_LOAD_DISTANCE || areaReference.arrayZ < 0 || areaReference.arrayZ > AREA_LOAD_DISTANCE);
  }

  public void requestArea(AreaReference areaReference) {
    Area area = areaReference.newArea();
    setAreaInternal(areaReference, area);
    requestGeneration(areaReference);
  }

  public void requestGeneration(AreaReference areaReference) {
    Area area = getAreaPlain(areaReference);
    gen.generate(area);
    new GenerationEvent(area, areaReference.getAreaCoordinates()).post();
    area.generated = true;
  }

  public Block getBlock(int x, int y, int z) {
    return getArea(BlockCoordinates.area(x), BlockCoordinates.area(y), BlockCoordinates.area(z)).getBlock(x, y, z);
  }

  public Block getBlock(BlockCoordinates blockCoordinates) {
    return getArea(blockCoordinates).getBlock(blockCoordinates.blockX, blockCoordinates.blockY, blockCoordinates.blockZ);
  }

  public void dispose() {

  }

}
