package ethanjones.cubes.server.integrated;

import ethanjones.cubes.common.Debug;
import ethanjones.cubes.common.Side;
import ethanjones.cubes.Cubes;
import ethanjones.cubes.server.CubesServer;

public abstract class IntegratedServer extends CubesServer implements Runnable {

  private Thread thread = null;

  @Override
  public void run() {
    try {
      create();

      while (!state.hasStopped()) {
        long l = System.currentTimeMillis() / Cubes.tickMS;
        render(); //render calls stop
        while ((System.currentTimeMillis() / Cubes.tickMS) == l) { //Only run once every "tickMS"
          try {
            Thread.sleep(1);
          } catch (Exception e) {
          }
        }
      }
    } catch (Exception e) {
      Debug.crash(e);
    }
  }

  @Override
  public void create() {
    super.create();
  }

  @Override
  public boolean isDedicated() {
    return false;
  }

  public Thread start() {
    if (thread != null) return thread;
    thread = new Thread(this);
    thread.setName(Side.Server.name());
    thread.setUncaughtExceptionHandler(Debug.UncaughtExceptionHandler.instance);
    thread.start();
    return thread;
  }
}
