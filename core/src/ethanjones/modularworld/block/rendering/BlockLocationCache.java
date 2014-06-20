package ethanjones.modularworld.block.rendering;

import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.core.events.EventHandler;
import ethanjones.modularworld.core.events.world.block.SetBlockEvent;
import ethanjones.modularworld.world.coordinates.AreaCoordinates;
import ethanjones.modularworld.world.coordinates.BlockCoordinates;
import ethanjones.modularworld.world.storage.Area;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockLocationCache {

  public final static int S = Area.S - 1;

  public HashMap<AreaCoordinates, List<BlockLocation>> cache;

  public BlockLocationCache() {
    cache = new HashMap<AreaCoordinates, List<BlockLocation>>();
    ModularWorld.instance.eventBus.register(this);
  }

  public List<BlockLocation> getBlockLocations(AreaCoordinates areaCoordinates) {
    List<BlockLocation> blockLocations = cache.get(areaCoordinates);
    if (blockLocations == null) {
      update(areaCoordinates);
      blockLocations = cache.get(areaCoordinates);
    }
    return blockLocations;
  }

  @EventHandler
  public void handleBlockChange(SetBlockEvent setBlockEvent) {
    BlockCoordinates blockCoordinates = setBlockEvent.getBlockCoordinates();
    update(new AreaCoordinates(blockCoordinates.areaX, blockCoordinates.areaY, blockCoordinates.areaZ));
  }

  boolean[][][] checked;

  public void update(AreaCoordinates areaCoordinates) {
    Area area = ModularWorld.instance.world.getArea(areaCoordinates);
    checked = new boolean[Area.S][Area.S][Area.S];
    List<BlockLocation> blockLocations = cache.get(areaCoordinates);
    if (blockLocations == null) {
      blockLocations = new ArrayList<BlockLocation>();
      cache.put(areaCoordinates, blockLocations);
    } else {
      blockLocations.clear();
    }
    for (int x = 0; x < Area.S; x++) {
      for (int y = 0; y < Area.S; y++) {
        for (int z = 0; z < Area.S; z++) {
          if (!checked[x][y][z] && area.getBlock(x, y, z) != null) {
            BlockLocation blockLocation = new BlockLocation();
            recursiveAdd(blockLocation, area, x, y, z);
            blockLocations.add(blockLocation);
          }
        }
      }
    }
  }

  public void recursiveAdd(BlockLocation blockLocation, Area area, int x, int y, int z) {
    if (blockLocation.matches(area.getBlock(x, y, z))) {
      blockLocation.add(x, y, z);
    }
    checked[x][y][z] = true;
    if (x != S) {
      recursiveAdd(blockLocation, area, x + 1, y, z);
    }
    if (x != 0) {
      recursiveAdd(blockLocation, area, x - 1, y, z);
    }

    if (y != S) {
      recursiveAdd(blockLocation, area, x, y + 1, z);
    }
    if (y != 0) {
      recursiveAdd(blockLocation, area, x, y - 1, z);
    }

    if (z != S) {
      recursiveAdd(blockLocation, area, x, y, z + 1);
    }
    if (z != 0) {
      recursiveAdd(blockLocation, area, x, y, z - 1);
    }
  }

  public void dispose() {
    ModularWorld.instance.eventBus.invalidate(this);
  }

}
