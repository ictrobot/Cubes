package ethanjones.cubes.side.client;

import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.system.Branding;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.graphics.world.AreaRenderer;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.storage.Area;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.Vector3;

public class ClientDebug {

  public static class IntAverage {

    public int current;
    public long total;
    public int num;
    public int average;

    public void add(int i) {
      current = i;
      total += i;
      num++;
      average = (int) (total / num);
    }
  }

  public static class FloatAverage {

    public float current;
    public long total;
    public int num;
    public float average;

    public void add(float i) {
      current = i;
      total += i;
      num++;
      average = (float) (total / (float) num);
    }
  }

  private static final String lineSeparator = System.getProperty("line.separator");
  static IntAverage fps = new IntAverage();
  static FloatAverage loop = new FloatAverage();
  //static long lastTime = System.currentTimeMillis();
  private static String debugString = "";

  public static void update() {
    Vector3 p = Cubes.getClient().player.position;
    loop.add(Gdx.graphics.getRawDeltaTime() * 1000f);
    //loop.add((int) (System.currentTimeMillis() - lastTime));
    //lastTime = System.currentTimeMillis();

    String str = Branding.VERSION_HASH;
    if (!str.isEmpty()) {
      str = "HASH: " + str + lineSeparator;
    }

    String performance = "FPS:" + fps.current + " AVG:" + fps.average + " MS:" + String.format("%.3f", loop.current) + " AVG:" + String.format("%.3f", loop.average) + " MEM:" + Compatibility.get().getFreeMemory() + "MB";
    String position = "POS X:" + String.format("%.3f", p.x) + "(" + CoordinateConverter.area(p.x) + ")" + " Y:" + String.format("%.3f", p.y) + "(" + CoordinateConverter.area(p.y) + ")" + " Z:" + String.format("%.3f", p.z) + "(" + CoordinateConverter.area(p.z) + ")";
    String direction = "DIR X:" + String.format("%.3f", Cubes.getClient().player.angle.x) + " Y:" + String.format("%.2f", Cubes.getClient().player.angle.y) + " Z:" + String.format("%.3f", Cubes.getClient().player.angle.z);
    String rendering = "R A:" + AreaRenderer.renderedThisFrame + " M:" + AreaRenderer.renderedMeshesThisFrame;
    String world = "W B:" + getBlockLight() + " S:" + getSunlight() + " T:" + Cubes.getClient().world.time;
    debugString = Branding.DEBUG + lineSeparator + performance + lineSeparator + position + lineSeparator + direction + lineSeparator + rendering + lineSeparator + world;

    GLProfiler.calls = 0;
    GLProfiler.drawCalls = 0;
    GLProfiler.shaderSwitches = 0;
    GLProfiler.textureBindings = 0;
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

  public static String getDebugString() {
    return debugString;
  }

  public static void tick() {
    fps.add(Gdx.graphics.getFramesPerSecond());
  }
}
