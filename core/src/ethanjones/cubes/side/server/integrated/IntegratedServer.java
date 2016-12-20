package ethanjones.cubes.side.server.integrated;

import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.server.CubesServer;
import ethanjones.cubes.world.save.Save;

public abstract class IntegratedServer extends CubesServer implements Runnable {

  private Thread thread = null;

  public IntegratedServer(Save save) {
    super(save);
  }

  @Override
  public void run() {
    try {
      create();
      loop();
      stop();
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
