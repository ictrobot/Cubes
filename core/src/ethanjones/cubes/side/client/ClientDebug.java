package ethanjones.cubes.side.client;

import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.system.Branding;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.CoordinateConverter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.Vector3;

public class ClientDebug {

  public static class Average {

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

  private static final String lineSeparator = System.getProperty("line.separator");
  static Average fps = new Average();
  static Average loop = new Average();
  static long lastTime = System.currentTimeMillis();
  private static String debugString = "";

  public static void update() {
    Vector3 p = Cubes.getClient().player.position;
    loop.add((int) (System.currentTimeMillis() - lastTime));
    lastTime = System.currentTimeMillis();

    String str = Branding.VERSION_HASH;
    if (!str.isEmpty()) {
      str = "HASH: " + str + lineSeparator;
    }

    String performance = "FPS:" + fps.current + " AVG:" + fps.average + " MS:" + String.format("%01d", loop.current) + " AVG:" + String.format("%01d", loop.average) + " MEM:" + Compatibility.get().getFreeMemory() + "MB";
    String position = "POS X:" + String.format("%.2f", p.x) + "(" + CoordinateConverter.area(p.x) + ")" + " Y:" + String.format("%.2f", p.y) + " Z:" + String.format("%.2f", p.z) + "(" + CoordinateConverter.area(p.z) + ")";
    String direction = "DIR X:" + String.format("%.2f", Cubes.getClient().player.angle.x) + " Y:" + String.format("%.2f", Cubes.getClient().player.angle.y) + " Z:" + String.format("%.2f", Cubes.getClient().player.angle.z);

    debugString = Branding.DEBUG + lineSeparator + performance + lineSeparator + position + lineSeparator + direction;

    GLProfiler.calls = 0;
    GLProfiler.drawCalls = 0;
    GLProfiler.shaderSwitches = 0;
    GLProfiler.textureBindings = 0;
  }

  public static String getDebugString() {
    return debugString;
  }

  public static void tick() {
    fps.add(Gdx.graphics.getFramesPerSecond());
  }
}
