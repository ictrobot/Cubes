package ethanjones.cubes.side.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import ethanjones.cubes.core.system.Branding;
import ethanjones.cubes.core.system.Memory;
import ethanjones.cubes.core.util.LongAverage;
import ethanjones.cubes.core.util.MathHelper;
import ethanjones.cubes.graphics.menu.Fonts;
import ethanjones.cubes.side.common.Cubes;

public class ClientDebug {

  public static class DebugLabel extends Label {

    static final LabelStyle style = new LabelStyle();

    static {
      style.font = Fonts.Size1;
    }

    public DebugLabel() {
      super(debugString, style);
    }

    public void validate() {
      setText(debugString);
      super.validate();
    }

  }

  private static final String lineSeparator = System.getProperty("line.separator");
  static LongAverage fps = new LongAverage();
  static LongAverage loop = new LongAverage();
  static long lastTime = System.currentTimeMillis();
  private static String debugString = "";

  public static void update() {
    Vector3 p = Cubes.getClient().player.position;
    fps.add(Gdx.graphics.getFramesPerSecond());
    loop.add(System.currentTimeMillis() - lastTime);
    lastTime = System.currentTimeMillis();

    String str = Branding.VERSION_HASH;
    if (!str.isEmpty()) {
      str = "HASH: " + str + lineSeparator;
    }

    debugString = Branding.DEBUG + lineSeparator + str + "FPS:" + fps.getCurrent() + " AFPS:" + fps.getAverage() + lineSeparator + "RAM T:" + Memory.max + "MB F:" + Memory.totalFree + "MB U:" + Memory.used + "MB" + lineSeparator + lineSeparator + "P X:" + String.format("%.2f", p.x) + " Y:" + String.format("%.2f", p.y) + " Z:" + String.format("%.2f", p.z) + lineSeparator + "A X:" + MathHelper.area(p.x) + " Y:" + MathHelper.area(p.y) + " Z:" + MathHelper.area(p.z) + lineSeparator + "Z X:" + MathHelper.zone(MathHelper.area(p.x)) + " Z:" + MathHelper.zone(MathHelper.area(p.z)) + lineSeparator + "D X:" + String.format("%02f", Cubes.getClient().player.angle.x) + " Y:" + String.format("%02f", Cubes.getClient().player.angle.y) + " Z:" + String.format("%02f", Cubes.getClient().player.angle.z) + lineSeparator + "L MS:" + String.format("%01d", loop.getCurrent()) + " AMS:" + String.format("%01d", loop.getAverage()) + lineSeparator + lineSeparator + "C:" + GLProfiler.calls + lineSeparator + "DC:" + GLProfiler.drawCalls + lineSeparator + "S:" + GLProfiler.shaderSwitches + lineSeparator + "T:" + GLProfiler.textureBindings + lineSeparator + "V:" + GLProfiler.vertexCount.latest + lineSeparator;

    GLProfiler.calls = 0;
    GLProfiler.drawCalls = 0;
    GLProfiler.shaderSwitches = 0;
    GLProfiler.textureBindings = 0;
  }
}
