package ethanjones.cubes.graphics.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.graphics.Graphics;
import ethanjones.cubes.graphics.assets.Assets;

import java.util.LinkedList;

import static ethanjones.cubes.graphics.Graphics.GUI_WIDTH;

public class FrametimeGraph {
  private static LinkedList<Float> frametimes = new LinkedList<Float>();
  private static Texture textureLine = Assets.getTexture("core:hud/FrametimeGraphLine.png");
  private static Texture textureLine60 = Assets.getTexture("core:hud/FrametimeGraphLine60.png");
  private static Texture textureBackground = Assets.getTexture("core:hud/FrametimeGraphBackground.png");
  private static final int scale = 10;
  private static final float yOffset = 50f;
  private static final ImmediateModeRenderer renderer = new ImmediateModeRenderer20(2000, false, false, 0, createShaderProgram());

  public static void drawLines(SpriteBatch batch) {
    if (!Settings.getBooleanSettingValue(Settings.DEBUG_FRAMETIME_GRAPH)) return;

    float width = width();

    batch.draw(textureBackground, 0, yOffset, width, scale * 32);
    while (frametimes.size() < width) {
      frametimes.addFirst(0f);
    }
    for (int i = 0; i <= 32; i++) {
      batch.draw(textureLine, 0, yOffset + (scale * i), width, 1);
    }
    batch.draw(textureLine60, 0, yOffset + (scale * 16.666666f), width, 1);
  }

  public static void drawPoints() {
    if (!Settings.getBooleanSettingValue(Settings.DEBUG_FRAMETIME_GRAPH)) return;

    renderer.begin(Graphics.screenViewport.getCamera().combined, GL20.GL_POINTS);
    for (int x = 0; x < Math.min(frametimes.size(), width()); x++) {
      float value = frametimes.get(x);
      if (value > 32) value = 32;
      float y = yOffset + (value * scale);
      renderer.vertex(x, y, 0);
    }
    renderer.end();
  }

  public static void update() {
    frametimes.addLast(Gdx.graphics.getRawDeltaTime() * 1000f);
    while (frametimes.size() > width()) {
      frametimes.removeFirst();
    }
  }
  
  private static float width() {
    return Math.min(GUI_WIDTH, 2000);
  }

  private static ShaderProgram createShaderProgram() {
    String vertex = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n";
    vertex += "uniform mat4 u_projModelView;\n";
    vertex += "void main() {\n" + "   gl_Position = u_projModelView * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n";
    vertex += "   gl_PointSize = 1.0;\n";
    vertex += "}\n";
    String fragment = "#ifdef GL_ES\n" + "precision mediump float;\n" + "#endif\n";
    fragment += "void main() {\n" + "   gl_FragColor = vec4(1, 0, 0, 1)"; //#00ff00ff
    fragment += ";\n}";
    return new ShaderProgram(vertex, fragment);
  }
}
