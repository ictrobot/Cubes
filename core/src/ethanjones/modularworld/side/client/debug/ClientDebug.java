package ethanjones.modularworld.side.client.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import ethanjones.modularworld.core.Branding;
import ethanjones.modularworld.core.ModularWorldException;
import ethanjones.modularworld.core.debug.Memory;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.core.util.LongAverage;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.world.coordinates.AreaCoordinates;
import ethanjones.modularworld.world.coordinates.BlockCoordinates;

public class ClientDebug {

  private static final DebugType[] types = DebugType.values();
  static LongAverage fps = new LongAverage();
  static LongAverage loop = new LongAverage();
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
    set(DebugType.ram, "RAM TF:" + Memory.totalFree + " F:" + Memory.free + " M:" + Memory.max + " A:" + Memory.max);
    Vector3 p = ModularWorldClient.instance.player.position;
    set(DebugType.coordinates, "P X:" + String.format("%.2f", p.x) + " Y:" + String.format("%.2f", p.y) + " Z:" + String.format("%.2f", p.z));
    set(DebugType.areaCoordinates, "A X:" + BlockCoordinates.area((int) Math.ceil(p.x)) + BlockCoordinates.area((int) Math.ceil(p.y)) + " Z:" + BlockCoordinates.area((int) Math.ceil(p.z)));
    set(DebugType.zoneCoordinates, "Z X:" + AreaCoordinates.zone((int) Math.ceil(p.x)) + " Z:" + AreaCoordinates.zone((int) Math.ceil(p.z)));
    set(DebugType.direction, "D X:" + ModularWorldClient.instance.player.angle.x + " Y:" + ModularWorldClient.instance.player.angle.y);
    loop.add((long) Gdx.graphics.getDeltaTime());
    set(DebugType.loop, "L MS:" + String.format("%03d", loop.getCurrent()) + " AMS:" + String.format("%03d", loop.getAverage()));
  }

  public static DebugLabel[] getLabels(Skin skin) {
    DebugLabel[] l = new DebugLabel[DebugType.values().length];
    for (int i = 0; i < DebugType.values().length; i++) {
      try {
        l[i] = new DebugLabel(DebugType.values()[i], skin);
      } catch (Exception e) {
        Log.error(new ModularWorldException("Failed to build debug screen", e));
      }
    }
    return l;
  }

  public static void set(DebugType debugType, String string) {
    if (debugType == null || string == null) return;
    debugData[debugType.ordinal()] = string;
  }

  public static String get(DebugType debugType) {
    if (debugType == null) return "";
    return debugData[debugType.ordinal()];
  }
}
