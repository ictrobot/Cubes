package ethanjones.cubes.world.generator.smooth;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.reference.BlockReference;
import ethanjones.cubes.world.server.WorldServer;
import ethanjones.cubes.world.storage.Area;

import com.badlogic.gdx.math.RandomXS128;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static ethanjones.cubes.world.generator.smooth.SmoothWorld.minSurfaceHeight;
import static ethanjones.cubes.world.generator.smooth.SmoothWorld.set;
import static ethanjones.cubes.world.generator.smooth.SmoothWorld.murmurHash3;

public class Cave {
  public static final int roomNodesMin = 25;
  public static final int roomNodesMax = 100;
  public static final int roomChangeXZConstant = 10;
  public static final int roomChangeXZRandom = 15;
  public static final int roomChangeY = 10;
  public static final int roomConnectDistanceMin = 5;
  public static final int roomConnectDistanceMax = 50;

  public final int caveStartX;
  public final int caveStartY;
  public final int caveStartZ;

  private final SmoothWorld smoothWorld;
  private final RandomXS128 numbers;

  private final ArrayList<RoomNode> rooms = new ArrayList<RoomNode>();
  private final ArrayList<TunnelNode> tunnels = new ArrayList<TunnelNode>();
  private final HashMap<AreaReference, HashSet<BlockReference>> blocks = new HashMap<AreaReference, HashSet<BlockReference>>();

  public Cave(int x, int z, SmoothWorld smoothWorld) {
    this.caveStartX = x;
    this.caveStartY = smoothWorld.getSurfaceHeight(x, z);
    this.caveStartZ = z;

    this.smoothWorld = smoothWorld;
    long l = x + z + (x * (x - 1)) + (z * (z + 1)) + (long) Math.pow(x, z > 0 ? z : (z < 0 ? -z : 1));
    this.numbers = new RandomXS128(smoothWorld.baseSeed, murmurHash3(smoothWorld.baseSeed + murmurHash3(l)));

    Log.debug("Generating new cave at " + x + "," + z + " (" + ((int) distanceFromSpawn(x, z)) + " blocks from spawn)");
    generateNodes();
    calculateBlocks();
  }

  public void generateNodes() {
    // make rooms
    rooms.add(new RoomNode(caveStartX, caveStartY, caveStartZ, 0));
    int num = 0;
    while (num <= roomNodesMax) {
      RoomNode randomNode = rooms.get(numbers.nextInt(rooms.size()));
      boolean noCheck = num <= (roomNodesMin / 2);

      int finalX = randomNode.location.blockX + getRoomChangeXZ();
      int finalZ = randomNode.location.blockZ + getRoomChangeXZ();

      if (inRange(finalX, finalZ)) {
        int offsetY = numbers.nextInt((roomChangeY * 2) + 1) - roomChangeY;
        int finalY = undergroundY(finalX, finalZ, randomNode.location.blockY, offsetY, noCheck);

        RoomNode roomNode = new RoomNode(finalX, finalY, finalZ, 2 + numbers.nextInt(3));
        roomNode.connect = randomNode;
        roomNode.noChecks = noCheck;
        rooms.add(roomNode);
      }
      if (num >= roomNodesMin && numbers.nextInt((roomNodesMax - roomNodesMin) - num) == 0) break;
      num++;
    }
    // make tunnels
    ArrayList<TunnelNode> straightTunnels = new ArrayList<TunnelNode>();
    for (int i = 0; i < rooms.size(); i++) {
      RoomNode roomNode = rooms.get(i);
      if (roomNode.connect != null) {
        TunnelNode tunnelNode = new TunnelNode(roomNode.location, roomNode.connect.location);
        tunnelNode.noChecks = roomNode.noChecks && roomNode.connect.noChecks;
        straightTunnels.add(tunnelNode);
      }

      int roomConnections = 0;
      for (int j = 0; j < 25; j++) {
        RoomNode other = rooms.get(numbers.nextInt(rooms.size()));
        if (other == roomNode || other == roomNode.connect) continue;

        float roomDistance = distance(roomNode.location, other.location);
        if (roomDistance < roomConnectDistanceMin || roomDistance > roomConnectDistanceMax) continue;

        straightTunnels.add(new TunnelNode(roomNode.location, other.location));
        roomConnections++;
        if (roomConnections == 2) break;
      }
    }
    // make bends in tunnels
    ArrayList<BlockReference> tunnelSections = new ArrayList<BlockReference>();
    for (TunnelNode tunnelNode : straightTunnels) {
      BlockReference a = tunnelNode.start;
      BlockReference b = tunnelNode.end;

      int dX = a.blockX - b.blockX;
      int dY = a.blockY - b.blockY;
      int dZ = a.blockZ - b.blockZ;
      int dist = (int) Math.sqrt(dX * dX + dY * dX + dZ * dZ);

      int numTurns = 1 + (dist / 10) + (dist / 5 == 0 ? 0 : numbers.nextInt(dist / 5));
      float turnXZChange = (float) Math.sqrt(dX * dX + dZ * dZ) / (float) numTurns;
      float turnYChange = (float) Math.abs(dY) / (float) numTurns;

      tunnelSections.clear();
      tunnelSections.add(a);
      for (int i = 1; i <= numTurns; i++) {
        float f = (float) i / ((float) numTurns + 1f);
        int x = b.blockX + (int) (dX * f);
        int y = b.blockY + (int) (dY * f);
        int z = b.blockZ + (int) (dZ * f);

        int randomX = (int) (((4 * numbers.nextFloat()) - 2f) * turnXZChange);
        int randomZ = (int) (((4 * numbers.nextFloat()) - 2f) * turnXZChange);

        int finalX = x + randomX;
        int finalZ = z + randomZ;
        int finalY = undergroundY(finalX, finalZ, y, (int) (((4 * numbers.nextFloat()) - 2f) * turnYChange), tunnelNode.noChecks);

        tunnelSections.add(new BlockReference().setFromBlockCoordinates(finalX, finalY, finalZ));
      }
      tunnelSections.add(b);

      for (int i = 0; i + 1 < tunnelSections.size(); i++) {
        tunnels.add(new TunnelNode(tunnelSections.get(i), tunnelSections.get(i + 1)));
      }
    }
  }

  private void calculateBlocks() {
    for (RoomNode room : rooms) {
      int roomX = room.location.blockX;
      int roomY = room.location.blockY;
      int roomZ = room.location.blockZ;
      int r = room.size;
      int r2 = r * r;

      for (int x = roomX - r; x <= roomX + r; x++) {
        for (int y = roomY - r; y <= roomY + r; y++) {
          for (int z = roomZ - r; z <= roomZ + r; z++) {
            int dX = roomX - x;
            int dY = roomY - y;
            int dZ = roomZ - z;
            if (dX * dX + dY * dY + dZ * dZ <= r2) {
              clear(x, y, z);
            }
          }
        }
      }
    }

    for (TunnelNode tunnel : tunnels) {
      int r = 2;

      int cX = tunnel.end.blockX - tunnel.start.blockX >= 0 ? 1 : -1;
      int cY = tunnel.end.blockY - tunnel.start.blockY >= 0 ? 1 : -1;
      int cZ = tunnel.end.blockZ - tunnel.start.blockZ >= 0 ? 1 : -1;

      int startX = tunnel.start.blockX - (cX * r);
      int startY = tunnel.start.blockY - (cY * r);
      int startZ = tunnel.start.blockZ - (cZ * r);

      int endX = tunnel.end.blockX + (cX * r);
      int endY = tunnel.end.blockY + (cY * r);
      int endZ = tunnel.end.blockZ + (cZ * r);

      BlockReference temp = new BlockReference();
      for (int x = startX; x <= endX; x += cX) {
        for (int y = startY; y <= endY; y += cY) {
          for (int z = startZ; z <= endZ; z += cZ) {
            temp.setFromBlockCoordinates(x, y, z);
            float radius;
            if (distance(tunnel.start, temp) < distance(tunnel.end, temp)) {
              radius = tunnel.startRadius;
            } else {
              radius = tunnel.endRadius;
            }
            if (distanceFromLine(tunnel.start, tunnel.end, temp.setFromBlockCoordinates(x, y, z)) <= radius) {
              clear(x, y, z);
            }
          }
        }
      }
    }
  }

  private int getRoomChangeXZ() {
    int rand = numbers.nextInt((roomChangeXZRandom * 2) + 1) - roomChangeXZRandom;
    return rand + (rand < 0 ? -roomChangeXZConstant : roomChangeXZConstant);
  }

  private int undergroundY(int x, int z, int prevY, int changeY, boolean noCheck) {
    int y = prevY + changeY;
    if (y < 10) return 10;
    if (y < minSurfaceHeight || noCheck) return y;

//  if (numbers.nextInt(32) == 0) return y;
//
//  int height = smoothWorld.getSurfaceHeight(x, z);
//  if (y >= height - 10) y = prevY - changeY;
//  if (y >= height - 10) y = height - 10 - Math.abs(changeY);

    int height = smoothWorld.getSurfaceHeight(x, z);
    while (y >= height - 6 && y >= 10) y--;
    return y;
  }

  private boolean inRange(int x, int z) {
    int dX = Math.abs(caveStartX - x);
    int dZ = Math.abs(caveStartZ - z);
    return dX < CaveManager.caveSafeBlockRadius && dZ < CaveManager.caveSafeBlockRadius;
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

  public float distanceFromSpawn(int x, int z) {
    BlockReference spawnPoint = smoothWorld.spawnPoint(((WorldServer) Cubes.getServer().world));
    int dX = x - spawnPoint.blockX;
    int dZ = z - spawnPoint.blockZ;
    return (float) Math.sqrt(dX * dX + dZ * dZ);
  }

  private static float distance(BlockReference a, BlockReference b) {
    int dX = a.blockX - b.blockX;
    int dY = a.blockY - b.blockY;
    int dZ = a.blockZ - b.blockZ;
    return (float) Math.sqrt(dX * dX + dY * dY + dZ * dZ);
  }

  private class TunnelNode {
    BlockReference start;
    BlockReference end;
    float startRadius = (float) (1.5f + (numbers.nextFloat() * 1.5));
    float endRadius = (float) (1.5f + (numbers.nextFloat() * 1.5));
    boolean noChecks;

    private TunnelNode(BlockReference start, BlockReference end) {
      this.start = start;
      this.end = end;
    }
  }

  private class RoomNode {
    RoomNode connect;
    BlockReference location;
    int size;
    boolean noChecks;

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
