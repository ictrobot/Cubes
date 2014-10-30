package ethanjones.cubes.core.platform.desktop;

public class LwjglLauncher {
  public static void main(String[] arg) {
    DesktopSecurityManager.setup();
    new LwjglCompatibility(arg).startCubes();
  }
}
