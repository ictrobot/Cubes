package ethanjones.modularworld.side.server;

import com.badlogic.gdx.utils.Disposable;
import ethanjones.modularworld.core.system.Debug;

public class ModularWorldServerThread extends Thread implements Disposable {

  private final ModularWorldServer modularWorldServer;
  private boolean running = false;

  public ModularWorldServerThread(ModularWorldServer modularWorldServer) {
    this.modularWorldServer = modularWorldServer;
  }

  @Override
  public void run() {
    running = true;
    try {
      modularWorldServer.create();

      while (running) {
        modularWorldServer.render();
      }

      modularWorldServer.dispose();
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
