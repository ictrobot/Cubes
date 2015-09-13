package ethanjones.cubes.side.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.system.Branding;
import ethanjones.cubes.graphics.menu.Fonts;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.CoordinateConverter;

public class ClientDebug {

  public static class DebugLabel extends Label {

    static final LabelStyle style = new LabelStyle();

    static {
      style.font = Fonts.Size3;
    }

    public DebugLabel() {
      super(debugString, style);
    }

    public void validate() {
      setText(debugString);
      super.validate();
    }

  }

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

    debugString = Branding.DEBUG + lineSeparator + str + "FPS:" + fps.current + " AFPS:" + fps.average + " MS:" + String.format("%01d", loop.current) + " AMS:" + String.format("%01d", loop.average) + lineSeparator + "RAM F:" + Compatibility.get().getFreeMemory() + "MB" + lineSeparator + lineSeparator + "P X:" + String.format("%.2f", p.x) + " Y:" + String.format("%.2f", p.y) + " Z:" + String.format("%.2f", p.z) + lineSeparator + "A X:" + CoordinateConverter.area(p.x) + " Z:" + CoordinateConverter.area(p.z) + lineSeparator + "D X:" + String.format("%02f", Cubes.getClient().player.angle.x) + " Y:" + String.format("%02f", Cubes.getClient().player.angle.y) + " Z:" + String.format("%02f", Cubes.getClient().player.angle.z) + lineSeparator + lineSeparator + "C:" + GLProfiler.calls + lineSeparator + "DC:" + GLProfiler.drawCalls + lineSeparator + "S:" + GLProfiler.shaderSwitches + lineSeparator + "T:" + GLProfiler.textureBindings + lineSeparator + "V:" + GLProfiler.vertexCount.latest + lineSeparator;

    GLProfiler.calls = 0;
    GLProfiler.drawCalls = 0;
    GLProfiler.shaderSwitches = 0;
    GLProfiler.textureBindings = 0;
  }

  public static void tick() {
    fps.add(Gdx.graphics.getFramesPerSecond());
  }
}
