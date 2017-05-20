package ethanjones.cubes.core.platform.html;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.util.Multimap;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.storage.WorldStorage;
import ethanjones.cubes.world.storage.WorldStorageInterface;
import ethanjones.data.Data;
import ethanjones.data.DataGroup;

import com.badlogic.gdx.utils.Base64Coder;
import com.eclipsesource.json.JsonArray;

import java.util.List;

public class HtmlWorldStorage implements WorldStorageInterface {
  private static Multimap<AreaReference, ChangedBlock> multimap = new Multimap<AreaReference, ChangedBlock>();
  
  @Override
  public native void openSave(String name)/*-{
      $wnd.cubesDB = new $wnd.Dexie(name);
      $wnd.cubesDB.version(1).stores({
         blocks: 'id++,[areaX+areaZ]'
      });
  }-*/;
  
  public native void deleteSave(String name)/*-{
      $wnd.cubesDB = undefined;
      $wnd.Dexie['delete'](name); // gwt sees .delete(), thinks 'delete' is a keyword, even though it's a totally valid method name is js...
  }-*/;
  
  @Override
  public native List<ChangedBlock> requestChangedBlocks(int areaX, int areaZ)/*-{
      $wnd.cubesDB.blocks.where({'areaX': areaX, 'areaZ': areaZ}).each(function (item) {
          @ethanjones.cubes.core.platform.html.HtmlWorldStorage::changedBlock(*)(item.areaX, item.areaZ, item.ref, item.block, item.data);
      }).then(function () {
          @ethanjones.cubes.core.platform.html.HtmlWorldStorage::changedBlockEnd(*)(areaX, areaZ);
      });
      return null;
  }-*/;
  
  
  public static void changedBlock(int areaX, int areaZ, int ref, int block, String data) {
    DataGroup d = null;
    if (data != null && !data.isEmpty()) {
      try {
        byte[] b = Base64Coder.decode(data, Base64Coder.urlsafeMap);
        d = (DataGroup) Data.input(b);
      } catch (Exception e) {
        Log.warning("Failed to read block data", e);
      }
    }
    multimap.put(new AreaReference().setFromAreaCoordinates(areaX, areaZ), new ChangedBlock(areaX, areaZ, ref, block, d));
  }
  
  public static void changedBlockEnd(int areaX, int areaZ) {
    List<ChangedBlock> changedBlocks = multimap.remove(new AreaReference().setFromAreaCoordinates(areaX, areaZ));
    if (changedBlocks.size() > 0) WorldStorage.processChangedBlocks(areaX, areaZ, changedBlocks);
  }
  
  @Override
  public void storeChangedBlocks(int areaX, int areaZ, List<ChangedBlock> changedBlocks) {
    _delete(areaX, areaZ);
    JsonArray blocks = new JsonArray();
    for (ChangedBlock b : changedBlocks) {
      if (b.areaX == areaX && b.areaZ == areaZ) {
        String base64 = null;
        if (b.data != null) {
          try {
            byte[] bytes = Data.output(b.data);
            base64 = new String(Base64Coder.encode(bytes, Base64Coder.urlsafeMap));
          } catch (Exception e) {
            Log.warning("Failed to write block data", e);
          }
        }
        blocks.add(new JsonArray().add(b.ref).add(b.blockAndMeta).add(base64));
      } else {
        Log.warning("Different areaX in changed block");
      }
    }
    _storeChangedBlocks(areaX, areaZ, blocks.toString());
  }
  
  private native static void _delete(int areaX, int areaZ)/*-{
      $wnd.cubesDB.blocks.where({'areaX': areaX, 'areaZ': areaZ})['delete']();
  }-*/;
  
  
  public native void _storeChangedBlocks(int areaX, int areaZ, String blocksJson)/*-{
      var blocks = $wnd.JSON.parse(blocksJson);
      for (var i = 0; i < blocks.length; i++) {
          var ref = blocks[i][0], block = blocks[i][1], data = blocks[i][2];
          if (data) {
              $wnd.cubesDB.blocks.add({
                  areaX: areaX,
                  areaZ: areaZ,
                  ref: ref,
                  block: block,
                  data: data
              });
          } else {
            $wnd.cubesDB.blocks.add({
                areaX: areaX,
                areaZ: areaZ,
                ref: ref,
                block: block
            });
          }
      }
      return null;
  }-*/;
  
  public static native boolean unavailable()/*-{
      // check local storage
      try {
          var storage = window.localStorage, x = '__storage_test__';
          storage.setItem(x, x);
          storage.removeItem(x);
      } catch(e) {
          return true;
      }
      return !$wnd.indexedDB || !$wnd.Dexie;
  }-*/;
}
