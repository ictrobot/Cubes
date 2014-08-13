package ethanjones.modularworld.core.debug;

public class Memory {

  public static volatile int max;
  public static volatile int allocated;
  public static volatile int free;
  public static volatile int totalFree;

  public static void update() {
    Runtime runtime = Runtime.getRuntime();

    // divide to get in MB
    max = (int) runtime.maxMemory() / 1048576;
    allocated = (int) runtime.totalMemory() / 1048576;
    free = (int) runtime.freeMemory() / 1048576;
    totalFree = (int) free + (max - allocated);
  }
}
