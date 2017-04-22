package ethanjones.cubes.world.storage;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.common.Side;
import ethanjones.cubes.world.storage.WorldStorageInterface.ChangedBlock;

import java.util.List;

public class WorldStorage {
  
  private static boolean setup;
  private static WorldStorageInterface wsi;
  
  public static void setInterface(WorldStorageInterface wsi) {
    if (setup || WorldStorage.wsi != null) return;
    WorldStorage.wsi = wsi;
  }
  
  public static WorldStorageInterface getInterface() {
    if (!setup) {
      setup = true;
      if (wsi == null) wsi = Compatibility.get().getWorldStorageInterface();
      if (wsi == null) Log.warning("No world storage available");
    }
    return wsi;
  }
  
  public static void openSave(String save) {
    if (getInterface() != null) wsi.openSave(save);
  }
  
  public static void deleteSave(String save) {
    if (getInterface() != null) wsi.deleteSave(save);
  }
  
  /**
   * Requests changed blocks, if request is async call processChangedBlocks when ready. Otherwise return results.
   */
  public static void requestChangedBlocks(int areaX, int areaZ) {
    if (getInterface() != null) {
      List<ChangedBlock> changedBlocks = wsi.requestChangedBlocks(areaX, areaZ);
      if (changedBlocks != null) processChangedBlocks(areaX, areaZ, changedBlocks);
    }
  }
  
  public static void processChangedBlocks(ChangedBlockBatch b) {
    if (b.processed) return;
    b.processed = true;
    processChangedBlocks(b.areaX, b.areaZ, b.changedBlocks);
  }
  
  public static void processChangedBlocks(int areaX, int areaZ, List<ChangedBlock> changedBlocks) {
    if (Cubes.getServer() == null || Cubes.getServer().world == null) return;
    if (!Side.isServer()) {
      Cubes.getServer().addChangedBlocksBatch(new ChangedBlockBatch(areaX, areaZ, changedBlocks));
      return;
    }
    Area area = Cubes.getServer().world.getArea(areaX, areaZ);
    if (area == null) {
      Log.warning("processChangedBlocks on non existent area");
      return;
    }
    for (ChangedBlock b : changedBlocks) {
      if (b.areaX == areaX && b.areaZ == areaZ) {
        area.setBlock(b.ref, b.blockAndMeta);
      } else {
        Log.warning("processChangedBlocks different areaX/areaZ");
      }
    }
  }
  
  /**
   All changed blocks must be in the same area
   **/
  public static void storeChangedBlocks(int areaX, int areaZ, List<ChangedBlock> changedBlock) {
    if (getInterface() != null) wsi.storeChangedBlocks(areaX, areaZ, changedBlock);
  }
  
  public static class ChangedBlockBatch {
    private final int areaX;
    private final int areaZ;
    private final List<ChangedBlock> changedBlocks;
    private boolean processed = false;
    
    public ChangedBlockBatch(int areaX, int areaZ, List<ChangedBlock> changedBlocks) {
      this.areaX = areaX;
      this.areaZ = areaZ;
      this.changedBlocks = changedBlocks;
    }
  }
}
