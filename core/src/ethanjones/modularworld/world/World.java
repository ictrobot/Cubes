package ethanjones.modularworld.world;

import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.block.Block;
import ethanjones.modularworld.core.events.world.generation.GenerationEvent;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.core.util.Maths;
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
  public final static int AREA_LOAD_DISTANCE_SQUARED = AREA_LOAD_DISTANCE * AREA_LOAD_DISTANCE;
  public final static int AREA_LOAD_DISTANCE_CUBED = AREA_LOAD_DISTANCE_SQUARED * AREA_LOAD_DISTANCE;
  public final static BlankArea BLANK_AREA = new BlankArea();
  public final WorldGenerator gen;
  public Area[] areasAroundPlayer;
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
    Log.info(AREA_LOAD_DISTANCE + " " + AREA_LOAD_DISTANCE_SQUARED + " " + AREA_LOAD_DISTANCE_CUBED);
    areasAroundPlayer = new Area[AREA_LOAD_DISTANCE_CUBED];
    areaReferencePool = new AreaReferencePool();
  }

  public int getArrayPos(int arrayX, int arrayY, int arrayZ) {
    return arrayX + arrayZ * AREA_LOAD_DISTANCE + arrayY * AREA_LOAD_DISTANCE_SQUARED;
  }

  public void playerChangedPosition() {
    playerArea.setFromVector3(ModularWorld.instance.player.position);
    if (playerArea.areaX - AREA_LOAD_RADIUS != minAreaX || playerArea.areaY - AREA_LOAD_RADIUS != minAreaY || playerArea.areaZ - AREA_LOAD_RADIUS != minAreaZ) {
      Area[] old = areasAroundPlayer;
      areasAroundPlayer = new Area[AREA_LOAD_DISTANCE_CUBED];
      for (int x = 0; x < AREA_LOAD_DISTANCE; x++) {
        for (int y = 0; y < AREA_LOAD_DISTANCE; y++) {
          for (int z = 0; z < AREA_LOAD_DISTANCE; z++) {
            Area o = old[getArrayPos(x, y, z)];
            if (o != null) {
              int nX = o.x - playerArea.areaX;
              int nY = o.y - playerArea.areaY;
              int nZ = o.z - playerArea.areaZ;
              if (Maths.fastPositive(nX) > AREA_LOAD_RADIUS || Maths.fastPositive(nY) > AREA_LOAD_RADIUS || Maths.fastPositive(nZ) > AREA_LOAD_RADIUS) {
                o.unload();
              } else {
                nX += AREA_LOAD_RADIUS;
                nY += AREA_LOAD_RADIUS;
                nZ += AREA_LOAD_RADIUS;
                areasAroundPlayer[getArrayPos(nX, nY, nZ)] = o;
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
      Area area = areasAroundPlayer[areaReference.arrayPos];
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
      areasAroundPlayer[areaReference.arrayPos] = area;
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
    areaReference.arrayPos = getArrayPos(areaReference.arrayX, areaReference.arrayY, areaReference.arrayZ);
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

  public void setBlock(Block block, int x, int y, int z) {
    getArea(BlockCoordinates.area(x), BlockCoordinates.area(y), BlockCoordinates.area(z)).setBlock(block, x, y, z);
  }

  public void setBlock(Block block, BlockCoordinates blockCoordinates) {
    getArea(blockCoordinates).setBlock(block, blockCoordinates.blockX, blockCoordinates.blockY, blockCoordinates.blockZ);
  }

  public void dispose() {

  }

}
