package ethanjones.modularworld.side.client.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import ethanjones.modularworld.core.ModularWorldException;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.core.util.LongAverage;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.world.coordinates.AreaCoordinates;
import ethanjones.modularworld.world.coordinates.BlockCoordinates;

public class Debug {

  private static final DebugType[] types = DebugType.values();
  static LongAverage fps = new LongAverage();
  static LongAverage renderer = new LongAverage();
  static LongAverage renderingLoop = new LongAverage();
  private static String[] debugData = new String[types.length];

  static {
    for (int i = 0; i < types.length; i++) {
      if (types[i].name().startsWith("blank")) {
        debugData[i] = "";
      }
    }
  }

  public static void set(DebugType debugType, String string) {
    if (debugType == null || string == null) return;
    debugData[debugType.ordinal()] = string;
  }

  public static String get(DebugType debugType) {
    if (debugType == null) return "";
    return debugData[debugType.ordinal()];
  }

  public static void version(String string) {
    set(DebugType.version, string);
  }

  public static void position() {
    Vector3 p = ModularWorldClient.instance.player.position;
    set(DebugType.coordinates, new StringBuilder().append("  X:").append(String.format("%.2f", p.x)).append(" Y:").append(String.format("%.2f", p.y)).append(" Z:").append(String.format("%.2f", p.z)).toString());
    set(DebugType.areaCoordinates, new StringBuilder().append("A X:").append(BlockCoordinates.area((int) Math.ceil(p.x))).append(" Y:").append(BlockCoordinates.area((int) Math.ceil(p.y))).append(" Z:").append(BlockCoordinates.area((int) Math.ceil(p.z))).toString());
    set(DebugType.zoneCoordinates, new StringBuilder().append("Z X:").append(AreaCoordinates.zone((int) Math.ceil(p.x))).append(" Z:").append(AreaCoordinates.zone((int) Math.ceil(p.z))).toString());
  }

  public static void fps() {
    fps.add(Gdx.graphics.getFramesPerSecond());
    StringBuilder s = new StringBuilder().append("FPS:").append(Gdx.graphics.getFramesPerSecond()).append(" AFPS:").append(fps.getAverage());
    set(DebugType.fps, s.toString());
  }

  public static void ram() {
    Runtime runtime = Runtime.getRuntime();

    int maxMemory = (int) runtime.maxMemory() / 1048576; // divide to get in MB
    int allocatedMemory = (int) runtime.totalMemory() / 1048576;
    int freeMemory = (int) runtime.freeMemory() / 1048576;

    StringBuilder sb = new StringBuilder();
    sb.append("R F:").append(freeMemory);
    sb.append(" TF:").append(freeMemory + (maxMemory - allocatedMemory));
    sb.append(" A:").append(allocatedMemory);
    sb.append(" M:").append(maxMemory);
    set(DebugType.ram, sb.toString());
  }

  public static void loop(long t) {
    renderingLoop.add(t);
    set(DebugType.loop, new StringBuilder().append("L MS:").append(String.format("%03d", t)).append(" AMS:").append(String.format("%03d", renderingLoop.getAverage())).toString());
  }

  public static void renderer(long t) {
    renderer.add(t);
    set(DebugType.rendering, new StringBuilder().append("R MS:").append(String.format("%03d", t)).append(" AMS:").append(String.format("%03d", renderer.getAverage())).toString());
    Debug.fps();
  }

  public static void facing() {
    set(DebugType.direction, ModularWorldClient.instance.player.angleX + " " + ModularWorldClient.instance.player.angleY);
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

  public static void printProperties() {
    Log.debug("Properties", "Java Home:          " + System.getProperty("java.home"));
    Log.debug("Properties", "Java Vendor:        " + System.getProperty("java.vendor"));
    Log.debug("Properties", "Java Vendor URL:    " + System.getProperty("java.vendor.url"));
    Log.debug("Properties", "Java Version:       " + System.getProperty("java.version"));
    Log.debug("Properties", "OS Name:            " + System.getProperty("os.name"));
    Log.debug("Properties", "OS Architecture:    " + System.getProperty("os.arch"));
    Log.debug("Properties", "OS Version:         " + System.getProperty("os.version"));
    Log.debug("Properties", "Working Directory:  " + System.getProperty("user.dir"));
    Log.debug("Properties", "User Home:          " + System.getProperty("user.home"));
  }
}
