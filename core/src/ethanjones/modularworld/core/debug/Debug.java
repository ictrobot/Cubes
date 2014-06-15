package ethanjones.modularworld.core.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.core.exception.DebugException;
import ethanjones.modularworld.core.util.LongAverage;

import java.util.EnumMap;

public class Debug {

    private static EnumMap<DebugType,String> debug = new EnumMap<DebugType, String>(DebugType.class);

    public static void set(DebugType debugType, String string) {
        debug.put(debugType, string);
    }

    public static String get(DebugType debugType) {
        return debug.get(debugType);
    }

    public static void version(String string) {
        set(DebugType.version,string);
    }

    static LongAverage fps = new LongAverage();
    public static void fps() {
        fps.add(Gdx.graphics.getFramesPerSecond());
        StringBuilder s = new StringBuilder().append("FPS:").append(Gdx.graphics.getFramesPerSecond()).append(" AFPS:").append(fps.getAverage());
       set(DebugType.fps, s.toString());
    }



    static LongAverage blockRenderer = new LongAverage();
    public static void blockRenderer(long t) {
        blockRenderer.add(t);
        renderer();
    }

    static LongAverage hudRenderer = new LongAverage();
    public static void hudRenderer(long t) {
        hudRenderer.add(t);
        renderer();
    }

    static LongAverage renderer = new LongAverage();
    public static void renderer(long t) {
        renderer.add(t);
        renderer();
    }

    private static void renderer() {
        StringBuilder s = new StringBuilder().append("C T:").append(renderer.getCurrent()).append(" B:").append((int) blockRenderer.getCurrent()).append(" H:").append((int) hudRenderer.getCurrent());
        set(DebugType.renderer, s.toString());
        s = new StringBuilder().append("A T:").append(renderer.getAverage()).append(" B:").append((int) blockRenderer.getAverage()).append(" H:").append((int) hudRenderer.getAverage());
        set(DebugType.rendererA, s.toString());
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
                throw new DebugException("Failed to build debug screen", e);
            }
        }
        return l;
    }
}
