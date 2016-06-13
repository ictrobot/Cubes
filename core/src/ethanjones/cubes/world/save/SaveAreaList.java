package ethanjones.cubes.world.save;

import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.LongMap.Entry;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SaveAreaList {
  private LongMap<byte[]> areas = new LongMap<byte[]>(5000);
  private int modCount = 0;

  public void setArea(int x, int y, byte[] b) {
    areas.put(getLong(x, y), b);
    modCount++;
  }

  public byte[] getArea(int x, int z) {
    return areas.get(getLong(x, z));
  }

  public void write(DataOutputStream stream) throws IOException {
    stream.writeLong(areas.size);
    for (Entry<byte[]> area : areas) {
      stream.writeLong(area.key);
      stream.write(area.value);
    }
  }

  public void read(DataInputStream stream) throws IOException {
    int size = (int) stream.readLong();
    areas.clear(size);
    modCount++;
    for (int i = 0; i < size; i++) {
      long l = stream.readLong();
      byte[] b = new byte[32];
      stream.readFully(b);
      areas.put(l, b);
    }
    modCount++;
  }

  public int getModCount() {
    return modCount;
  }

  public static long getLong(int x, int z) {
    return (((long) x) << 32) | (z & 0xffffffffL);
  }

  public static int getX(long l) {
    return (int) (l >> 32);
  }

  public static int getZ(long l) {
    return (int) l;
  }
}
