package ethanjones.cubes.core.platform.desktop;

public class ClientLauncher implements DesktopLauncher {

  public static void main(String[] arg) {
    new ClientLauncher(arg).start();
  }

  private void start() {
    DesktopCompatibility.setup();
    new ClientCompatibility(this, arg).startCubes();
  }

  private final String[] arg;

  private ClientLauncher(String[] arg) {
    this.arg = arg;
  }
}
