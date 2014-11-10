package ethanjones.cubes.core.platform.desktop;

public class LwjglLauncher implements DesktopLauncher {

  private final String[] arg;

  private LwjglLauncher(String[] arg) {
    this.arg = arg;
  }

  public static void main(String[] arg) {
    new LwjglLauncher(arg).start();
  }

  private void start() {
    DesktopSecurityManager.setup();
    new LwjglCompatibility(this, arg).startCubes();
  }
}
