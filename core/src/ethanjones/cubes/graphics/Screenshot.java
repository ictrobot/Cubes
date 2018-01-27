package ethanjones.cubes.graphics;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.settings.Setting;
import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.core.settings.type.DropDownSetting;
import ethanjones.cubes.core.system.CubesException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Screenshot {

  private static final AtomicReference<ScreenshotMethod> takingScreenshot = new AtomicReference<ScreenshotMethod>(null);
  private static final String[] resolutions = new String[]{"normal", "1080p", "1440p", "4k", "8k", "16k", "max"};
  private static final HashMap<String, int[]> resolutionMap = new HashMap<String, int[]>();

  static {
    resolutionMap.put("1080p", new int[]{1920, 1080});
    resolutionMap.put("1440p", new int[]{2560, 1440});
    resolutionMap.put("4k", new int[]{3840, 2160});
    resolutionMap.put("8k", new int[]{7680, 4320});
    resolutionMap.put("16k", new int[]{15360, 8640});
  }

  public static Setting screenshotResolutionSetting() {
    return new DropDownSetting(resolutions) {
      @Override
      public void onChange() {
        if ("normal".equals(selected) || "max".equals(selected)) return;

        IntBuffer intBuffer = BufferUtils.newIntBuffer(2);
        Gdx.gl20.glGetIntegerv(GL20.GL_MAX_VIEWPORT_DIMS, intBuffer);
        int maxWidth = intBuffer.get(0);
        int maxHeight = intBuffer.get(1);

        int[] resolution = resolutionMap.get(selected);
        if (resolution[0] > maxWidth || resolution[1] > maxHeight) selected = "normal";
      }
    };
  }

  public static void startScreenshot() {
    String setting = ((DropDownSetting) Settings.getSetting(Settings.GRAPHICS_SCREENSHOT_SIZE)).getSelected();
    ScreenshotMethod method = "normal".equals(setting) ? new NormalResolutionScreenshot() : new HighResolutionScreenshot();

    if (takingScreenshot.compareAndSet(null, method)) {
      method.frameStart();
    }
  }

  public static void endScreenshot() {
    ScreenshotMethod method = takingScreenshot.getAndSet(null);
    if (method != null) method.frameEnd();
  }

  private static void writeScreenshot(Pixmap pixmap) {
    FileHandle dir = Compatibility.get().getBaseFolder().child("screenshots");
    dir.mkdirs();
    FileHandle f = dir.child(System.currentTimeMillis() + ".png");
    try {
      PixmapIO.PNG writer = new PixmapIO.PNG((int) (pixmap.getWidth() * pixmap.getHeight() * 1.5f));
      try {
        writer.setFlipY(true);
        writer.write(f, pixmap);
      } finally {
        writer.dispose();
      }
    } catch (IOException ex) {
      throw new CubesException("Error writing PNG: " + f, ex);
    } finally {
      pixmap.dispose();
    }
    Log.info("Took screenshot '" + f.file().getAbsolutePath() + "'");
  }

  private interface ScreenshotMethod {
    void frameStart();
    void frameEnd();
  }

  private static class NormalResolutionScreenshot implements ScreenshotMethod {

    @Override
    public void frameStart() {

    }

    @Override
    public void frameEnd() {
      Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, Graphics.RENDER_WIDTH, Graphics.RENDER_HEIGHT);
      writeScreenshot(pixmap);
    }
  }

  private static class HighResolutionScreenshot implements ScreenshotMethod {
    AtomicInteger state = new AtomicInteger(0);
    int screenshotWidth = 0;
    int screenshotHeight = 0;
    int oldWidth = 0;
    int oldHeight = 0;
    FrameBuffer frameBuffer = null;

    @Override
    public void frameStart() {
      if (!state.compareAndSet(0,  1)) return;

      String setting = ((DropDownSetting) Settings.getSetting(Settings.GRAPHICS_SCREENSHOT_SIZE)).getSelected();
      if ("max".equals(setting)) {
        IntBuffer intBuffer = BufferUtils.newIntBuffer(2);
        Gdx.gl20.glGetIntegerv(GL20.GL_MAX_VIEWPORT_DIMS, intBuffer);
        screenshotWidth = intBuffer.get(0);
        screenshotHeight = intBuffer.get(1);
      } else {
        screenshotWidth = Screenshot.resolutionMap.get(setting)[0];
        screenshotHeight = Screenshot.resolutionMap.get(setting)[1];
      }

      Log.debug("Attempting to take " + setting + " (" + screenshotWidth + "x" + screenshotHeight + ") resolution screenshot");
      frameBuffer = new FrameBuffer(Pixmap.Format.RGB888, screenshotWidth, screenshotHeight, true);
      frameBuffer.begin();

      oldWidth = Graphics.RENDER_WIDTH;
      oldHeight = Graphics.RENDER_HEIGHT;
      Graphics.RENDER_WIDTH = screenshotWidth;
      Graphics.RENDER_HEIGHT = screenshotHeight;
      Adapter.getInterface().resize(screenshotWidth, screenshotHeight);
    }

    @Override
    public void frameEnd() {
      if (!state.compareAndSet(1,  2)) return;

      Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, screenshotWidth, screenshotHeight);
      frameBuffer.end();

      Graphics.RENDER_WIDTH = oldWidth;
      Graphics.RENDER_HEIGHT = oldHeight;
      Adapter.getInterface().resize(oldWidth, oldHeight);

      writeScreenshot(pixmap);
    }
  }

}
