package ethanjones.cubes.world.save;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.world.storage.Area;

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
      area.read(dataInputStream, false);
      dataInputStream.close();
      return area;
    } catch (Exception e) {
      Log.error("Failed to read area", e);
      return null;
    }
  }

  public static boolean write(Save save, Area area) {
    if (!area.isReady()) return false;
    if (!area.modifiedSinceSave()) return false;
    area.saveModCount();

    Deflater deflater = deflaterThreadLocal.get();

    FileHandle file = file(save, area.areaX, area.areaZ);
    try {
      deflater.reset();
      OutputStream stream = file.write(false, 8192);
      DeflaterOutputStream deflaterStream = new DeflaterOutputStream(stream, deflater);
      BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(deflaterStream);
      DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
      area.write(dataOutputStream, false, false); //TODO resize when writing
      bufferedOutputStream.flush();
      deflaterStream.finish();
      stream.close();
    } catch (Exception e) {
      Log.error(e);
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
    return zMostSignificant.child(Integer.toString(z & 0xFFFF));
  }
}
