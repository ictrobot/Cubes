package ethanjones.cubes.world.save;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.world.storage.Area;
import ethanjones.cubes.world.storage.AreaMap;
import ethanjones.data.DataGroup;

import com.badlogic.gdx.files.FileHandle;

import java.io.*;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class SaveAreaIO {
  private static final ThreadLocal<Deflater> deflaterThreadLocal = new ThreadLocal<Deflater>() {
    @Override
    protected Deflater initialValue() {
      return new Deflater();
    }
  };
  private static final ThreadLocal<Inflater> inflaterThreadLocal = new ThreadLocal<Inflater>() {
    @Override
    protected Inflater initialValue() {
      return new Inflater() {
        @Override
        public void end() {
          // do nothing, as android calls inflater.end() when closing InflaterInputStream
        }
      };
    }
  };

  public static Area read(Save save, int x, int z) {
    FileHandle file = file(save, x, z);

    if (!file.exists()) {
      //Log.warning("Area does not exist");
      return null;
    }

    Inflater inflater = inflaterThreadLocal.get();

    try {
      inflater.reset();
      InputStream inputStream = file.read(8192);
      InflaterInputStream inflaterInputStream = new InflaterInputStream(inputStream, inflater);
      BufferedInputStream bufferedInputStream = new BufferedInputStream(inflaterInputStream);
      DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
      Area area = new Area(x, z);
      area.read(dataInputStream);
      dataInputStream.close();
      return area;
    } catch (Exception e) {
      Log.error("Failed to read area " + x + "," + z, e);
      return null;
    }
  }

  public static boolean write(Save save, Area area) {
    if (save.readOnly) return false;
    if (!area.isReady()) return false;
  
    AreaMap map = area.areaMap();
    DataGroup[] dataGroups;
    if (map == null || map.world == null || map.world.entities == null) {
      dataGroups = new DataGroup[0];
    } else {
      dataGroups = map.world.entities.getEntitiesForSave(area.areaX, area.areaZ);
    }
    if (!area.modifiedSinceSave(dataGroups)) return false;
    area.saveModCount();

    Deflater deflater = deflaterThreadLocal.get();

    FileHandle file = file(save, area.areaX, area.areaZ);
    try {
      deflater.reset();
      OutputStream stream = file.write(false, 8192);
      DeflaterOutputStream deflaterStream = new DeflaterOutputStream(stream, deflater);
      BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(deflaterStream);
      DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
      area.writeSave(dataOutputStream, dataGroups);
      bufferedOutputStream.flush();
      deflaterStream.finish();
      stream.close();
    } catch (Exception e) {
      Log.error("Failed to write area " + area.areaX + "," + area.areaZ, e);
      return false;
    }

    return true;
  }

  public static FileHandle file(Save save, int x, int z) {
    FileHandle folderArea = save.folderArea();
    FileHandle xMostSignificant = folderArea.child(Integer.toString(x & 0xFFFF0000));
    FileHandle xLeastSignificant = xMostSignificant.child(Integer.toString(x & 0xFFFF));
    FileHandle zMostSignificant = xLeastSignificant.child(Integer.toString(z & 0xFFFF0000));
    zMostSignificant.mkdirs();
    Compatibility.get().nomedia(zMostSignificant);
    
    return zMostSignificant.child(Integer.toString(z & 0xFFFF));
  }
}
