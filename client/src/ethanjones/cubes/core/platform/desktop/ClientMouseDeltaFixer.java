package ethanjones.cubes.core.platform.desktop;

import ethanjones.cubes.core.system.CubesException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Input;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;

import java.lang.reflect.Field;

/**
 * This class changes Gdx.input.getDelta*() methods to return correct delta mouse values between the previous frame
 * and the next frame.
 * <p>
 * Lwjgl3Input's cursorPosCallback may be called multiple times by GLFW. This is mainly a problem when the cursor is
 * captured as it may be called >15 times a frame, whereas otherwise it may be called 2 times at most a frame. Each
 * time it is called it updates the previous mouse position and delta values, meaning that Gdx.input.getDelta*() only
 * returns the most recent delta value from the last call from GLFW.
 * <p>
 * https://github.com/libgdx/libgdx/blob/7b8d832f1ecb21a0853551ac60a2c2a1e7daaed3/backends/gdx-backend-lwjgl3/src/com/badlogic/gdx/backends/lwjgl3/Lwjgl3Input.java#L101-L104
 * <p>
 * Before libGDX 1.9.9, CameraController's touchDragged would have been run multiple times and actually used the same
 * last delta values each time. Although this was inaccurate the result was close enough to the correct value at a high
 * enough frame rate. This class is designed to more accurately track delta values, and to work on libGDX >= 1.9.9.
 * <p>
 * This class uses a second GLFWCursorPosCallback which keeps track of the delta values separately and also calls the
 * original libGDX callback to maintain compatibility. It also then replaces libGDX's incorrect delta values with the
 * correct values using reflection.
 */
class ClientMouseDeltaFixer {

  private static double deltaX, deltaY;

  private static Field deltaXField;
  private static Field deltaYField;
  private static GLFWCursorPosCallback libgdxCallback;

  static void setup() {
    // Log.debug("Setting up Client Mouse Delta Fixer");

    try {
      deltaXField = Lwjgl3Input.class.getDeclaredField("deltaX");
      deltaXField.setAccessible(true);
      deltaYField = Lwjgl3Input.class.getDeclaredField("deltaY");
      deltaYField.setAccessible(true);

      Field f = Lwjgl3Input.class.getDeclaredField("cursorPosCallback");
      f.setAccessible(true);
      libgdxCallback = (GLFWCursorPosCallback) f.get(Gdx.input);
    } catch (ReflectiveOperationException | ClassCastException e) {
      throw new CubesException("Failed to setup reflection to inject correct delta mouse values!", e);
    }

    GLFW.glfwSetCursorPosCallback(((Lwjgl3Graphics) Gdx.graphics).getWindow().getWindowHandle(), cursorPosCallback);
  }

  private static final GLFWCursorPosCallback cursorPosCallback = new GLFWCursorPosCallback() {
    private double logicalMouseY, logicalMouseX;
    private long frameID = 0;

    @Override
    public void invoke(long windowHandle, double x, double y) {
      if (Gdx.graphics.getFrameId() != frameID) {
        deltaX = deltaY = 0;
        // frameID incremented in window.update() before GLFW.glfwPollEvents()
        frameID = Gdx.graphics.getFrameId();
        // post runnable runs after GLFW.glfwPollEvents()
        Gdx.app.postRunnable(ClientMouseDeltaFixer::injectValues);
      }
      synchronized (cursorPosCallback) {
        deltaX += x - logicalMouseX;
        deltaY += y - logicalMouseY;
        logicalMouseX = x;
        logicalMouseY = y;
      }
      libgdxCallback.invoke(windowHandle, x, y);
    }
  };

  private static void injectValues() {
    try {
      deltaXField.setInt(Gdx.input, (int) deltaX);
      deltaYField.setInt(Gdx.input, (int) deltaY);
    } catch (ReflectiveOperationException e) {
      throw new CubesException("Failed to use reflection to inject correct delta mouse values!", e);
    }
  }
}
