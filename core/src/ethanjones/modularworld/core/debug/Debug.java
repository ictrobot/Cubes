package ethanjones.modularworld.core.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.core.ModularWorldException;
import ethanjones.modularworld.core.util.LongAverage;
import ethanjones.modularworld.world.coordinates.AreaCoordinates;
import ethanjones.modularworld.world.coordinates.BlockCoordinates;

import java.util.EnumMap;

public class Debug {

  static LongAverage fps = new LongAverage();
  static LongAverage blockRenderer = new LongAverage();
  static LongAverage hudRenderer = new LongAverage();
  static LongAverage renderer = new LongAverage();
  static LongAverage renderingLoop = new LongAverage();
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
    Vector3 p = ModularWorld.instance.player.position;
    set(DebugType.coordinates, new StringBuilder().append("  X:").append(String.format("%.2f", p.x)).append(" Y:").append(String.format("%.2f", p.y)).append(" Z:").append(String.format("%.2f", p.z)).toString());
    set(DebugType.areaCoordinates, new StringBuilder().append("A X:").append(BlockCoordinates.area((int) Math.ceil(p.x))).append(" Y:").append(BlockCoordinates.area((int) Math.ceil(p.y))).append(" Z:").append(BlockCoordinates.area((int) Math.ceil(p.z))).toString());
    set(DebugType.zoneCoordinates, new StringBuilder().append("Z X:").append(AreaCoordinates.zone((int) Math.ceil(p.x))).append(" Z:").append(AreaCoordinates.zone((int) Math.ceil(p.z))).toString());
  }

  public static void fps() {
    fps.add(Gdx.graphics.getFramesPerSecond());
    StringBuilder s = new StringBuilder().append("FPS:").append(Gdx.graphics.getFramesPerSecond()).append(" AFPS:").append(fps.getAverage());
    set(DebugType.fps, s.toString());
  }

  public static void renderingLoop(long t) {
    renderingLoop.add(t);
    set(DebugType.renderingLoop, new StringBuilder().append("T MS:").append(String.format("%03d", t)).append(" AMS:").append(String.format("%03d", renderingLoop.getAverage())).toString());
  }

  public static void renderer(long t) {
    renderer.add(t);
    set(DebugType.renderingAll, new StringBuilder().append("R MS:").append(String.format("%03d", t)).append(" AMS:").append(String.format("%03d", renderer.getAverage())).toString());
    Debug.fps();
  }

  public static void blockRenderer(long t, int renderedNum, int renderedChunks, int totalChunks) {
    blockRenderer.add(t);
    set(DebugType.renderingBlock, new StringBuilder().append("B MS:").append(String.format("%03d", t)).append(" AMS:").append(String.format("%03d", blockRenderer.getAverage())).append(" N:").append(renderedNum).append(" C:").append(renderedChunks).append("/").append(totalChunks).toString());
  }

  public static void hudRenderer(long t) {
    hudRenderer.add(t);
    set(DebugType.renderingHud, new StringBuilder().append("H MS:").append(String.format("%03d", t)).append(" AMS:").append(String.format("%03d", hudRenderer.getAverage())).toString());
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
        throw new ModularWorldException("Failed to build debug screen", e);
      }
    }
    return l;
  }
}
