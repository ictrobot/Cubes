package ethanjones.cubes.core.platform.desktop;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.system.Debug;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class DesktopDeadlockDetector extends Thread {

  private static final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
  private static DesktopDeadlockDetector INSTANCE;

  private DesktopDeadlockDetector() {
    setDaemon(true);
    setName("DesktopDeadlockDetector");
    start();
  }

  private void check() {
    long[] deadlockedThreadIds = threadMXBean.findDeadlockedThreads();

    if (deadlockedThreadIds != null) {
      Log.error("Deadlock detected!");
      try {
        ThreadInfo[] info = threadMXBean.getThreadInfo(deadlockedThreadIds);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < info.length; i++) {
          if (info[i] == null) continue;
          stringBuilder.append(info[i].getThreadName());
          if (i < info.length - 1) stringBuilder.append(", ");
        }
        Log.error("Threads: " + stringBuilder.toString());
      } catch (Exception ignored) {

      }
      Debug.crash(new CubesException("Deadlock detected!"));
    }
  }

  @Override
  public void run() {
    while (true) {
      try {
        check();
      } catch (UnsupportedOperationException ignored) {

      }
      try {
        Thread.sleep(2000);
      } catch (InterruptedException ignored) {

      }
    }
  }

  static void setup() {
    if (INSTANCE == null) INSTANCE = new DesktopDeadlockDetector();
  }
}
