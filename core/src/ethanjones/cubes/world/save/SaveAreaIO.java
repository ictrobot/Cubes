package ethanjones.cubes.world.save;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.networking.stream.DirectByteArrayOutputStream;
import ethanjones.cubes.world.storage.Area;

import com.badlogic.gdx.files.FileHandle;

import java.io.*;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class SaveAreaIO {
  private static final char[] hex = "0123456789ABCDEF".toCharArray();
  private static final ThreadLocal<ThreadData> local = new ThreadLocal<ThreadData>() {
    @Override
    protected ThreadData initialValue() {
      return new ThreadData();
    }
  };

  public static Area read(Save save, int x, int z, byte[] hash) {
    String firstByte = bytesToHex(hash, 0, 1);
    String allBytes = bytesToHex(hash, 0, 32);

    FileHandle folderArea = save.folderArea();
    FileHandle subDirectory = folderArea.child(firstByte);
    FileHandle file = subDirectory.child(allBytes);

    if (!file.exists()) {
      Log.warning("Area does not exist");
      return null;
    }

    ThreadData data = local.get();

    try {
      data.inflater.reset();
      InputStream inputStream = file.read(8192);
      InflaterInputStream inflaterInputStream = new InflaterInputStream(inputStream, data.inflater);
      DataInputStream dataInputStream = new DataInputStream(inflaterInputStream);
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

    data.md.reset();
    data.md.update(data.stream.buffer(), 0, data.stream.count());
    try {
      data.md.digest(data.hash, 0, data.hash.length);
    } catch (DigestException e) {
      Log.error(e);
      return false;
    }

    String firstByte = bytesToHex(data.hash, 0, 1);
    String allBytes = bytesToHex(data.hash, 0, 32);

    FileHandle folderArea = save.folderArea();
    FileHandle subDirectory = folderArea.child(firstByte);
    FileHandle file = subDirectory.child(allBytes);

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
      save.getSaveAreaList().setArea(area.areaX, area.areaZ, data.hash.clone());
    }

    return write;
  }

  public static String bytesToHex(byte[] data, int start, int length) {
    StringBuilder builder = new StringBuilder(data.length * 2);
    int end = start + length;
    for (int i = start; i < end; i++) {
      byte b = data[i];
      builder.append(hex[(b >> 4) & 0xF]).append(hex[(b & 0xF)]);
    }
    return builder.toString();
  }

  private static class ThreadData {
    final DirectByteArrayOutputStream stream = new DirectByteArrayOutputStream();
    final DataOutputStream dataOutputStream = new DataOutputStream(stream);
    final MessageDigest md;
    final Deflater deflater = new Deflater();
    final Inflater inflater = new Inflater();
    final byte[] hash = new byte[32];

    public ThreadData() {
      try {
        md = MessageDigest.getInstance("SHA-256");
      } catch (NoSuchAlgorithmException e) {
        throw new CubesException(e);
      }
    }
  }
}
