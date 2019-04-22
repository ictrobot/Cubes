package ethanjones.cubes.graphics.world.area;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Vector3;
import ethanjones.cubes.side.common.Cubes;

import java.util.ArrayList;

public class DebugLineRenderer {

  static class Line {
    final Vector3 start = new Vector3();
    final Vector3 end = new Vector3();
    Color color = Color.WHITE;
  }

  static ArrayList<Line> lines = new ArrayList<Line>();
  static Mesh mesh;
  static float[] vertices;
  static final ImmediateModeRenderer renderer = new ImmediateModeRenderer20(2000, false, true, 0);


  public static void addLine(Vector3 start, Vector3 end, Color color) {
    Line l = new Line();
    l.start.set(start);
    l.end.set(end);
    l.color = color;
    lines.add(l);
  }

  public static void render() {
    if (lines.size() == 0) return;

    renderer.begin(Cubes.getClient().renderer.worldRenderer.camera.combined, GL20.GL_LINES);
    for (Line line : lines) {
      renderer.color(line.color);
      renderer.vertex(line.start.x, line.start.y, line.start.z);
      renderer.vertex(line.end.x, line.end.y, line.end.z);
    }
    renderer.end();
    lines.clear();
  }
}
