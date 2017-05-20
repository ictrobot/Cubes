package ethanjones.cubes.world.storage;


import ethanjones.data.DataGroup;

import java.util.List;

public interface WorldStorageInterface {
  
  void openSave(String name);
  
  void deleteSave(String name);
  
  /**
   * Requests changed blocks, if request is async call WorldStorage.processChangedBlocks when ready (and return null). Otherwise return results.
   */
  List<ChangedBlock> requestChangedBlocks(int areaX, int areaZ);
  
  /**
  All changed blocks must be in the same area
   **/
  void storeChangedBlocks(int areaX, int areaZ, List<ChangedBlock> changedBlock);
  
  class ChangedBlock {
    public int areaX;
    public int areaZ;
    public int ref;
    public int blockAndMeta;
    public DataGroup data;
    
    public ChangedBlock() {
    }
    
    public ChangedBlock(int areaX, int areaZ, int ref, int blockAndMeta) {
      this.areaX = areaX;
      this.areaZ = areaZ;
      this.ref = ref;
      this.blockAndMeta = blockAndMeta;
      this.data = null;
    }
  
    public ChangedBlock(int areaX, int areaZ, int ref, int blockAndMeta, DataGroup data) {
      this.areaX = areaX;
      this.areaZ = areaZ;
      this.ref = ref;
      this.blockAndMeta = blockAndMeta;
      this.data = data;
    }
  }
}
