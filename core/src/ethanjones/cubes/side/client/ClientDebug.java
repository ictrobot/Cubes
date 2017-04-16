package ethanjones.cubes.side.client;

import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.system.Branding;
import ethanjones.cubes.core.util.PerSecond;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.graphics.world.AreaRenderer;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.collision.BlockIntersection;
import ethanjones.cubes.world.storage.Area;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.WindowedMean;

public class ClientDebug {

  private static final String lineSeparator = System.getProperty("line.separator");
  static WindowedMean ms = new WindowedMean(50);
  private static PerSecond fps = new PerSecond(10);
  private static StringBuilder builder = new StringBuilder(250).append(Branding.DEBUG).append(lineSeparator);
  private static int brandingDebugLength = builder.length();
  
  private static String twoDP(double d) {
    int i = (int) Math.ceil(d);
    int f = (int) ((d * 10) % 10);
    if (f < 0) f *= -1;
    int s = (int) ((d * 100) % 10);
    if (s < 0) s *= -1;
    return i + "." + f + s;
  }
  
  private static String oneDP(double d) {
    int i = (int) Math.ceil(d);
    int f = (int) ((d * 10) % 10);
    if (f < 0) f *= -1;
    return i + "." + f;
  }
  
  protected static void frame() {
    fps.tick();
  }

  public static String getDebugString() {
    Vector3 p = Cubes.getClient().player.position;
    ms.addValue(Gdx.graphics.getRawDeltaTime() * 1000f);

    builder.setLength(brandingDebugLength);
    builder.append("FPS:").append(Gdx.graphics.getFramesPerSecond()).append(" MS:").append(twoDP(ms.getMean())).append(" MEM:").append(Compatibility.get().getFreeMemory()).append("MB").append(lineSeparator);
    builder.append("TPS C:").append(Cubes.getClient().ticksPerSecond.last()).append(" A:").append(oneDP(Cubes.getClient().ticksPerSecond.average()));
    if (Cubes.getServer() != null) builder.append(" S:").append(Cubes.getServer().ticksPerSecond.last()).append(" A:").append(oneDP(Cubes.getServer().ticksPerSecond.average()));
    builder.append(lineSeparator);
    builder.append("POS X:").append(twoDP(p.x)).append("(").append(CoordinateConverter.area(p.x)).append(")").append(" Y:").append(twoDP(p.y)).append("(").append(CoordinateConverter.area(p.y)).append(")").append(" Z:").append(twoDP(p.z)).append("(").append(CoordinateConverter.area(p.z)).append(")").append(lineSeparator);
    builder.append("DIR X:").append(twoDP(Cubes.getClient().player.angle.x)).append(" Y:").append(twoDP(Cubes.getClient().player.angle.y)).append(" Z:").append(twoDP(Cubes.getClient().player.angle.z)).append(lineSeparator);
    builder.append("R A:").append(AreaRenderer.renderedThisFrame).append(" M:").append(AreaRenderer.renderedMeshesThisFrame).append(lineSeparator);
    builder.append("W B:").append(getBlockLight()).append(" S:").append(getSunlight()).append(" T:").append(Cubes.getClient().world.getTime());
    BlockIntersection blockIntersection = BlockIntersection.getBlockIntersection(Cubes.getClient().player.position, Cubes.getClient().player.angle, Cubes.getClient().world);
    if (blockIntersection != null && blockIntersection.getBlock() != null) {
      builder.append(lineSeparator).append("B ID:").append(blockIntersection.getBlock().id).append(" M:").append(blockIntersection.getBlockMeta());
    }
    return builder.toString();
  }

  private static int getBlockLight() {
    Player player = Cubes.getClient().player;
    Area area = Cubes.getClient().world.getArea(CoordinateConverter.area(player.position.x), CoordinateConverter.area(player.position.z));
    if (area != null) {
      int x = CoordinateConverter.block(player.position.x);
      int y = CoordinateConverter.block(player.position.y - player.height);
      int z = CoordinateConverter.block(player.position.z);
      if (y > area.maxY || y < 0) return 0;
      return area.getLight(x - area.minBlockX, y, z - area.minBlockZ);
    }
    return 0;
  }

  private static int getSunlight() {
    Player player = Cubes.getClient().player;
    Area area = Cubes.getClient().world.getArea(CoordinateConverter.area(player.position.x), CoordinateConverter.area(player.position.z));
    if (area != null) {
      int x = CoordinateConverter.block(player.position.x);
      int y = CoordinateConverter.block(player.position.y - player.height);
      int z = CoordinateConverter.block(player.position.z);
      if (y > area.maxY || y < 0) return 0;
      return area.getSunlight(x - area.minBlockX, y, z - area.minBlockZ);
    }
    return 0;
  }
}
