package ethanjones.cubes.core.platform.desktop;

public class ServerLauncher implements DesktopLauncher {

  private final String[] arg;

  private ServerLauncher(String[] arg) {
    this.arg = arg;
  }

  public static void main(String[] arg) {
    new ServerLauncher(arg).start();
  }

  private void start() {
    DesktopCompatibility.setup();
    new ServerCompatibility(this, arg).startCubes();
  }
}
