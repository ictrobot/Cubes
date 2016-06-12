package ethanjones.cubes.side.server.integrated;

import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.server.CubesServer;
import ethanjones.cubes.world.save.Save;

public abstract class IntegratedServer extends CubesServer implements Runnable {

  private Thread thread = null;

  public IntegratedServer() {
    super();
  }

  public IntegratedServer(Save save) {
    super(save);
  }

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
