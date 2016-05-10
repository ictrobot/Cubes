package ethanjones.cubes.world.generator.smooth;

import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.reference.BlockReference;
import ethanjones.cubes.world.storage.Area;

import com.badlogic.gdx.math.RandomXS128;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static ethanjones.cubes.world.generator.smooth.SmoothWorld.set;
import static ethanjones.cubes.world.generator.smooth.SmoothWorld.murmurHash3;

public class Cave {
  public static final int minNode = 25;
  public static final int maxNode = 100;
  public static final int roomChangeXZ = 25;
  public static final int roomChangeY = 10;
  public static final int roomConnectMax = 50;

  private final int blockX;
  private final int blockY;
  private final int blockZ;

  private final SmoothWorld smoothWorld;
  private final RandomXS128 numbers;

  private ArrayList<RoomNode> room = new ArrayList<RoomNode>();
  private ArrayList<TunnelNode> tunnel = new ArrayList<TunnelNode>();
  private HashMap<AreaReference, HashSet<BlockReference>> blocks = new HashMap<AreaReference, HashSet<BlockReference>>();

  public Cave(int x, int z, SmoothWorld smoothWorld) {
    this.blockX = x;
    this.blockY = smoothWorld.getSurfaceHeight(x, z);
    this.blockZ = z;

    this.smoothWorld = smoothWorld;
    long l = x + z + (x * (x - 1)) + (z * (z + 1)) + (long) Math.pow(x, z > 0 ? z : (z < 0 ? -z : 1));
    this.numbers = new RandomXS128(murmurHash3(smoothWorld.baseSeed), murmurHash3(murmurHash3(smoothWorld.baseSeed) + murmurHash3(l)));

    generateNodes();
    calculateBlocks();
  }

  public void generateNodes() {
    // make rooms
    room.add(new RoomNode(blockX, blockY, blockZ, 0));
    int num = 0;
    while (num <= maxNode) {
      RoomNode randomNode = room.get(numbers.nextInt(room.size()));

      int offsetX = numbers.nextInt((roomChangeXZ * 2) + 1) - roomChangeXZ;
      int offsetY = numbers.nextInt((roomChangeY * 2) + 1) - roomChangeY;
      int offsetZ = numbers.nextInt((roomChangeXZ * 2) + 1) - roomChangeXZ;

      int locX = randomNode.location.blockX + offsetX;
      int locZ = randomNode.location.blockZ + offsetZ;

      int height = smoothWorld.getSurfaceHeight(locX, locZ);
      int locY = randomNode.location.blockY + offsetY;
      if (locY >= height - 5) locY = randomNode.location.blockY - offsetY;
      if (locY >= height - 5) locY = height - Math.abs(offsetY);

      RoomNode roomNode = new RoomNode(locX, locY, locZ, 2 + numbers.nextInt(3));
      roomNode.connect = randomNode;
      room.add(roomNode);
      if (num >= minNode && numbers.nextInt(maxNode - num) == 0) break;
      num++;
    }
    // make tunnels
    ArrayList<TunnelNode> sTunnel = new ArrayList<TunnelNode>();
    for (int i = 0; i < room.size(); i++) {
      RoomNode roomNode = room.get(i);
      if (roomNode.connect != null) {
        sTunnel.add(new TunnelNode(roomNode.location, roomNode.connect.location));
      }
      int connections = 0;
      for (int j = 0; j < 25; j++) {
        RoomNode other = room.get(numbers.nextInt(room.size()));
        if (other == roomNode || other == roomNode.connect) continue;
        if (distance(roomNode.location, other.location) > roomConnectMax) continue;
        sTunnel.add(new TunnelNode(roomNode.location, other.location));
        connections++;
        if (connections == 2) break;
      }
    }
    // make bends in tunnels
    ArrayList<BlockReference> temp = new ArrayList<BlockReference>();
    for (TunnelNode tunnelNode : sTunnel) {
      BlockReference a = tunnelNode.start;
      BlockReference b = tunnelNode.end;

      int dX = a.blockX - b.blockX;
      int dY = a.blockY - b.blockY;
      int dZ = a.blockZ - b.blockZ;

      int dist = (int) Math.sqrt(dX * dX + dY * dX + dZ * dZ);
      int numTurn = 1 + (dist / 20) + (dist / 15 == 0 ? 0 : numbers.nextInt(dist / 15));

      temp.clear();
      temp.add(a);
      for (int i = 0; i < numTurn; i++) {
        int tX = (int) (dX * ((float) i / (float) numTurn));
        int tY = (int) (dY * ((float) i / (float) numTurn));
        int tZ = (int) (dZ * ((float) i / (float) numTurn));
        int tXZMax = Math.max(Math.abs(tX), Math.abs(tZ));

        int x = b.blockX + tX;
        int y = b.blockY + tY;
        int z = b.blockZ + tZ;

        int oX = (int) (((2 * numbers.nextFloat()) - 1f) * tXZMax);
        int oY = (int) (((2 * numbers.nextFloat()) - 1f) * tY);
        int oZ = (int) (((2 * numbers.nextFloat()) - 1f) * tXZMax);

        temp.add(new BlockReference().setFromBlockCoordinates(x + oX, y + oY, z + oZ));
      }
      temp.add(b);

      for (int i = 0; i + 1 < temp.size(); i++) {
        tunnel.add(new TunnelNode(temp.get(i), temp.get(i + 1)));
      }
    }
  }

  private void calculateBlocks() {
    for (RoomNode node : room) {
      int rX = node.location.blockX;
      int rY = node.location.blockY;
      int rZ = node.location.blockZ;
      int r = node.size;
      int r2 = r * r;
      for (int x = rX - r; x <= rX + r; x++) {
        for (int y = rY - r; y <= rY + r; y++) {
          for (int z = rZ - r; z <= rZ + r; z++) {
            int cX = rX - x;
            int cY = rY - y;
            int cZ = rZ - z;
            if (cX * cX + cY * cY + cZ * cZ <= r2) {
              clear(x, y, z);
            }
          }
        }
      }
    }

    for (TunnelNode node : tunnel) {
      int r = 2;

      int cX = node.end.blockX - node.start.blockX >= 0 ? 1 : -1;
      int cY = node.end.blockY - node.start.blockY >= 0 ? 1 : -1;
      int cZ = node.end.blockZ - node.start.blockZ >= 0 ? 1 : -1;

      int sX = node.start.blockX - (cX * r);
      int sY = node.start.blockY - (cY * r);
      int sZ = node.start.blockZ - (cZ * r);

      int eX = node.end.blockX + (cX * r);
      int eY = node.end.blockY + (cY * r);
      int eZ = node.end.blockZ + (cZ * r);

      BlockReference temp = new BlockReference();
      for (int x = sX; x <= eX; x += cX) {
        for (int y = sY; y <= eY; y += cY) {
          for (int z = sZ; z <= eZ; z += cZ) {
            temp.setFromBlockCoordinates(x, y, z);
            float radius;
            if (distance(node.start, temp) < distance(node.end, temp)) {
              radius = node.startRadius;
            } else {
              radius = node.endRadius;
            }
            if (distanceFromLine(node.start, node.end, temp.setFromBlockCoordinates(x, y, z)) <= radius) {
              clear(x, y, z);
            }
          }
        }
      }
    }
  }

  public void apply(Area area) {
    HashSet<BlockReference> blank = blocks.get(new AreaReference().setFromArea(area));
    if (blank == null) return;
    for (BlockReference b : blank) {
      set(area, null, b.blockX - area.minBlockX, b.blockY, b.blockZ - area.minBlockZ);
    }
  }

  private static float distanceFromLine(BlockReference start, BlockReference end, BlockReference location) {
    float a = distance(start, location);
    float b = distance(end, location);
    float c = distance(start, end);

    float s = (a + b + c) / 2f;
    float area = (float) Math.sqrt(s * (s - a) * (s - b) * (s - c));

    return (area * 2) / c;
  }

  private static float distance(BlockReference a, BlockReference b) {
    int dX = a.blockX - b.blockX;
    int dY = a.blockY - b.blockY;
    int dZ = a.blockZ - b.blockZ;
    return (float) Math.sqrt(dX * dX + dY * dY + dZ * dZ);
  }

  private void clear(int blockX, int blockY, int blockZ) {
    if (blockY <= 0) return;
    AreaReference areaReference = new AreaReference().setFromBlockCoordinates(blockX, blockZ);
    HashSet<BlockReference> b = blocks.get(areaReference);
    if (b == null) {
      b = new HashSet<BlockReference>();
      blocks.put(areaReference, b);
    }
    b.add(new BlockReference().setFromBlockCoordinates(blockX, blockY, blockZ));
  }

  private class TunnelNode {
    BlockReference start;
    BlockReference end;
    float startRadius = (float) (1 + (numbers.nextFloat() * 1.5));
    float endRadius = (float) (1 + (numbers.nextFloat() * 1.5));

    private TunnelNode(BlockReference start, BlockReference end) {
      this.start = start;
      this.end = end;
    }
  }

  private class RoomNode {
    RoomNode connect;
    BlockReference location;
    int size;

    private RoomNode(int blockX, int blockY, int blockZ, int size) {
      this.location = new BlockReference().setFromBlockCoordinates(blockX, blockY, blockZ);
      this.size = size;
    }

    @Override
    public String toString() {
      return location.toString() + "    " + size;
    }
  }
}
