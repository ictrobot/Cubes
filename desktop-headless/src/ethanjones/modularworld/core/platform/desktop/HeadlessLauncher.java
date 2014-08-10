package ethanjones.modularworld.core.platform.desktop;

public class HeadlessLauncher {
  public static void main(String[] arg) {
    new HeadlessCompatibility(arg).startModularWorld();
  }
}
