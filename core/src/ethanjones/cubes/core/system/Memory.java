package ethanjones.cubes.core.system;

public class Memory {

  public static volatile int max;
  public static volatile int allocated;
  public static volatile int free;
  public static volatile int totalFree;
  public static volatile int used;
  public static final String unit = "MB";
  private static final int divideMB = 1048576; //1024 * 1024. Divide to get MB

  static {
    update();
  }

  public synchronized static void update() {
    Runtime runtime = Runtime.getRuntime();

    max = (int) runtime.maxMemory() / divideMB;
    allocated = (int) runtime.totalMemory() / divideMB;
    free = (int) runtime.freeMemory() / divideMB;
    totalFree = free + (max - allocated);
    used = max - totalFree;
  }
}
