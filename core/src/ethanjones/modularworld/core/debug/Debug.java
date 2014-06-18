package ethanjones.modularworld.core.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.core.exception.CustomException;
import ethanjones.modularworld.core.util.LongAverage;
import ethanjones.modularworld.world.coordinates.Coordinates;

import java.util.EnumMap;

public class Debug {

  private static EnumMap<DebugType, String> debug = new EnumMap<DebugType, String>(DebugType.class);

  static {
    for (DebugType d : DebugType.values()) {
      if (d.name().startsWith("blank")) {
        debug.put(d, "");
      }
    }
  }

  public static void set(DebugType debugType, String string) {
    debug.put(debugType, string);
  }

  public static String get(DebugType debugType) {
    return debug.get(debugType);
  }

  public static void version(String string) {
    set(DebugType.version, string);
  }

  public static void position() {
    Coordinates coordinates = new Coordinates(ModularWorld.instance.player.position);
    set(DebugType.coordinates, new StringBuilder().append("  X:").append(String.format("%.2f", coordinates.x)).append(" Y:").append(String.format("%.2f", coordinates.y)).append(" Z:").append(String.format("%.2f", coordinates.z)).toString());
    set(DebugType.areaCoordinates, new StringBuilder().append("A X:").append(coordinates.areaX).append(" Y:").append(coordinates.areaY).append(" Z:").append(coordinates.areaZ).toString());
    set(DebugType.zoneCoordinates, new StringBuilder().append("Z X:").append(coordinates.zoneX).append(" Z:").append(coordinates.zoneZ).toString());
  }

  static LongAverage fps = new LongAverage();

  public static void fps() {
    fps.add(Gdx.graphics.getFramesPerSecond());
    StringBuilder s = new StringBuilder().append("FPS:").append(Gdx.graphics.getFramesPerSecond()).append(" AFPS:").append(fps.getAverage());
    set(DebugType.fps, s.toString());
  }


  static LongAverage blockRenderer = new LongAverage();

  public static void blockRenderer(long t, int renderedNum, int renderedChunks, int totalChunks) {
    blockRenderer.add(t);
    set(DebugType.renderingBlock, new StringBuilder().append("B MS:").append(String.format("%03d", t)).append(" AMS:").append(String.format("%03d", blockRenderer.getAverage())).append(" N:").append(renderedNum).append(" C:").append(renderedChunks).append("/").append(totalChunks).toString());
  }

  static LongAverage hudRenderer = new LongAverage();

  public static void hudRenderer(long t) {
    hudRenderer.add(t);
    set(DebugType.renderingHud, new StringBuilder().append("H MS:").append(String.format("%03d", t)).append(" AMS:").append(String.format("%03d", hudRenderer.getAverage())).toString());
  }

  static LongAverage renderer = new LongAverage();

  public static void renderer(long t) {
    renderer.add(t);
    set(DebugType.renderingTotal, new StringBuilder().append("T MS:").append(String.format("%03d", t)).append(" AMS:").append(String.format("%03d", renderer.getAverage())).toString());
    Debug.fps();
  }

  public static void facing() {
    set(DebugType.direction, ModularWorld.instance.player.angleX + " " + ModularWorld.instance.player.angleY);
  }

  public static DebugLabel[] getLabels(Skin skin) {
    DebugLabel[] l = new DebugLabel[DebugType.values().length];
    for (int i = 0; i < DebugType.values().length; i++) {
      try {
        l[i] = new DebugLabel(DebugType.values()[i], skin);
      } catch (Exception e) {
        throw new CustomException("Failed to build debug screen", e);
      }
    }
    return l;
  }
}
