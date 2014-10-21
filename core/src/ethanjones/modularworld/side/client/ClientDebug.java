package ethanjones.modularworld.side.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import ethanjones.modularworld.core.Branding;
import ethanjones.modularworld.core.debug.Memory;
import ethanjones.modularworld.core.util.LongAverage;
import ethanjones.modularworld.graphics.menu.Fonts;
import ethanjones.modularworld.world.coordinates.AreaCoordinates;
import ethanjones.modularworld.world.coordinates.BlockCoordinates;

public class ClientDebug {

  private static final DebugType[] types = DebugType.values();
  static LongAverage fps = new LongAverage();
  static LongAverage loop = new LongAverage();
  static long lastTime = System.currentTimeMillis();
  private static String[] debugData = new String[types.length];

  static {
    for (int i = 0; i < types.length; i++) {
      if (types[i].name().startsWith("blank")) {
        debugData[i] = "";
      }
    }
  }

  public static void setup() {
    set(DebugType.version, Branding.DEBUG);
  }

  public static void update() {
    fps.add(Gdx.graphics.getFramesPerSecond());
    set(DebugType.fps, "FPS:" + fps.getCurrent() + " AFPS:" + fps.getAverage());
    set(DebugType.ram, "RAM F:" + Memory.totalFree + "MB U:" + Memory.used + "MB");
    Vector3 p = ModularWorldClient.instance.player.position;
    set(DebugType.coordinates, "P X:" + String.format("%.2f", p.x) + " Y:" + String.format("%.2f", p.y) + " Z:" + String.format("%.2f", p.z));
    set(DebugType.areaCoordinates, "A X:" + BlockCoordinates.area((int) Math.ceil(p.x)) + " Y:" + BlockCoordinates.area((int) Math.ceil(p.y)) + " Z:" + BlockCoordinates.area((int) Math.ceil(p.z)));
    set(DebugType.zoneCoordinates, "Z X:" + AreaCoordinates.zone((int) Math.ceil(p.x)) + " Z:" + AreaCoordinates.zone((int) Math.ceil(p.z)));
    set(DebugType.direction, "D X:" + ModularWorldClient.instance.player.angle.x + " Y:" + ModularWorldClient.instance.player.angle.y);
    loop.add(System.currentTimeMillis() - lastTime);
    lastTime = System.currentTimeMillis();
    set(DebugType.loop, "L MS:" + String.format("%03d", loop.getCurrent()) + " AMS:" + String.format("%03d", loop.getAverage()));

    set(DebugType.calls, "C:" + GLProfiler.calls);
    set(DebugType.drawCalls, "DC:" + GLProfiler.drawCalls);
    set(DebugType.shaderSwitches, "S:" + GLProfiler.shaderSwitches);
    set(DebugType.textureBindings, "T:" + GLProfiler.textureBindings);
    set(DebugType.vertexCount, "V:" + GLProfiler.vertexCount.latest);
    GLProfiler.calls = 0;
    GLProfiler.drawCalls = 0;
    GLProfiler.shaderSwitches = 0;
    GLProfiler.textureBindings = 0;
  }

  public static void set(DebugType debugType, String string) {
    if (debugType == null || string == null) return;
    debugData[debugType.ordinal()] = string;
  }

  public static String get(DebugType debugType) {
    if (debugType == null) return "";
    return debugData[debugType.ordinal()];
  }

  public static enum DebugType {
    version,
    fps,
    ram,
    blank1,
    coordinates,
    areaCoordinates,
    zoneCoordinates,
    direction,
    blank2,
    loop,
    blank3,
    calls,
    drawCalls,
    shaderSwitches,
    textureBindings,
    vertexCount
  }

  public static class DebugLabel extends Label {

    static final LabelStyle style = new LabelStyle();

    static {
      style.font = Fonts.Size1;
    }

    public DebugLabel() {
      super("", style);
      update();
    }

    public DebugLabel update() {
      StringBuilder s = new StringBuilder();
      for (DebugType debugType : DebugType.values()) {
        String str = get(debugType);
        if (str != null) {
          s.append(str).append(System.lineSeparator());
        }
      }
      setText(s.toString());
      setBounds(0, Gdx.graphics.getHeight() - getPrefHeight(), getPrefWidth(), getPrefHeight());
      return this;
    }

    public void validate() {
      this.update();
      super.validate();
    }

  }
}
