package ethanjones.cubes.world.generator.smooth;

import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.storage.Area;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

public class Cave {
  public final int caveStartX;
  public final int caveStartY;
  public final int caveStartZ;

  private final HashMap<AreaReference, int[]> blocks;

  public Cave(int x, int y, int z, HashMap<AreaReference, int[]> blocks) {
    this.caveStartX = x;
    this.caveStartY = y;
    this.caveStartZ = z;

    this.blocks = blocks;
  }

  public void apply(Area area) {
    int[] array = blocks.get(new AreaReference().setFromArea(area));
    if (array == null) return;
    for (int ref : array) {
      if (ref < area.blocks.length) area.blocks[ref] = 0;
    }
  }

  public void write(DataOutputStream outputStream) throws IOException {
    outputStream.writeInt(caveStartX);
    outputStream.writeInt(caveStartY);
    outputStream.writeInt(caveStartZ);
    outputStream.writeInt(blocks.size());

    for (Entry<AreaReference, int[]> entry : blocks.entrySet()) {
      AreaReference key = entry.getKey();
      int[] value = entry.getValue();

      outputStream.writeInt(key.areaX);
      outputStream.writeInt(key.areaZ);
      outputStream.writeInt(value.length);

      for (int i = 0; i < value.length; i++) {
        outputStream.writeInt(value[i]);
      }
    }
  }

  public static Cave read(DataInputStream inputStream) throws IOException {
    int caveStartX = inputStream.readInt();
    int caveStartY = inputStream.readInt();
    int caveStartZ = inputStream.readInt();
    int mapSize = inputStream.readInt();

    HashMap<AreaReference, int[]> blocks = new HashMap<AreaReference, int[]>();

    for (int i = 0; i < mapSize; i++) {
      int areaX = inputStream.readInt();
      int areaZ = inputStream.readInt();
      int valueLength = inputStream.readInt();

      AreaReference areaReference = new AreaReference().setFromAreaCoordinates(areaX, areaZ);
      int[] value = new int[valueLength];

      for (int j = 0; j < valueLength; j++) {
        value[j] = inputStream.readInt();
      }

      blocks.put(areaReference, value);
    }
    return new Cave(caveStartX, caveStartY, caveStartZ, blocks);
  }
}
