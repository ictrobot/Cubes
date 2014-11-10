package ethanjones.cubes.core.platform.desktop;

public class HeadlessLauncher implements DesktopLauncher {

  private final String[] arg;

  private HeadlessLauncher(String[] arg) {
    this.arg = arg;
  }

  public static void main(String[] arg) {
    new HeadlessLauncher(arg).start();
  }

  private void start() {
    DesktopSecurityManager.setup();
    new HeadlessCompatibility(this, arg).startCubes();
  }
}
