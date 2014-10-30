package ethanjones.cubes.side.server;

import com.badlogic.gdx.utils.Disposable;

import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.common.Cubes;

public class CubesServerThread extends Thread implements Disposable {

  private final CubesServer server;
  private boolean running = false;

  public CubesServerThread(CubesServer server) {
    this.server = server;
    this.server.thread = this;
    setName(Side.Server.name());
  }

  @Override
  public void run() {
    running = true;
    try {
      server.create();

      while (running) {
        long l = System.currentTimeMillis() / Cubes.tickMS;
        server.render();
        while ((System.currentTimeMillis() / Cubes.tickMS) == l) { //Only run once every "tickMS"
          try {
            sleep(1);
          } catch (Exception e) {
          }
        }
      }

      server.dispose();
    } catch (Exception e) {
      running = false;
      Debug.crash(e);
    }
  }

  @Override
  public void dispose() {
    running = false;
  }
}
