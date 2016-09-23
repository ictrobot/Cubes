package ethanjones.cubes.world.save;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.networking.stream.DirectByteArrayOutputStream;
import ethanjones.cubes.world.storage.Area;

import com.badlogic.gdx.files.FileHandle;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class SaveAreaIO {
  private static final ThreadLocal<ThreadData> local = new ThreadLocal<ThreadData>() {
    @Override
    protected ThreadData initialValue() {
      return new ThreadData();
    }
  };

  public static Area read(Save save, int x, int z) {
    FileHandle file = file(save, x, z);

    if (!file.exists()) {
      //Log.warning("Area does not exist");
      return null;
    }

    ThreadData data = local.get();

    try {
      data.inflater.reset();
      InputStream inputStream = file.read(8192);
      InflaterInputStream inflaterInputStream = new InflaterInputStream(inputStream, data.inflater);
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

  protected static boolean write(Save save, Area area) {
    if (!area.isReady()) return false;
    if (!area.modifiedSinceSave()) return false;
    area.saveModCount();

    ThreadData data = local.get();

    data.stream.reset();
    try {
      area.write(data.dataOutputStream, false, false); //TODO resize when writing
    } catch (IOException e) {
      Log.error(e);
      return false;
    }

//    data.md.reset();
//    data.md.update(data.stream.buffer(), 0, data.stream.count());
//    try {
//      data.md.digest(data.hash, 0, data.hash.length);
//    } catch (DigestException e) {
//      Log.error(e);
//      return false;
//    }

    FileHandle file = file(save, area.areaX, area.areaZ);

    boolean write = !file.exists();
    if (write) {
      try {
        data.deflater.reset();
        OutputStream stream = file.write(false);
        DeflaterOutputStream deflaterStream = new DeflaterOutputStream(stream, data.deflater);
        deflaterStream.write(data.stream.buffer(), 0, data.stream.count());
        deflaterStream.finish();
        stream.close();
      } catch (Exception e) {
        Log.error(e);
        return false;
      }
    }

    return write;
  }

  private static class ThreadData {
    final DirectByteArrayOutputStream stream = new DirectByteArrayOutputStream();
    final DataOutputStream dataOutputStream = new DataOutputStream(stream);
    final MessageDigest md;
    final Deflater deflater = new Deflater();
    final Inflater inflater = new Inflater() {
      @Override
      public void end() {
        // do nothing, as android calls inflater.end() when closing InflaterInputStream
      }
    };
    final byte[] hash = new byte[32];

    public ThreadData() {
      try {
        md = MessageDigest.getInstance("SHA-256");
      } catch (NoSuchAlgorithmException e) {
        throw new CubesException(e);
      }
    }
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
