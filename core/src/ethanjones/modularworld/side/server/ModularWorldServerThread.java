package ethanjones.modularworld.side.server;

import com.badlogic.gdx.utils.Disposable;
import ethanjones.modularworld.core.system.Debug;
import ethanjones.modularworld.side.Side;
import ethanjones.modularworld.side.common.ModularWorld;

public class ModularWorldServerThread extends Thread implements Disposable {

  private final ModularWorldServer modularWorldServer;
  private boolean running = false;

  public ModularWorldServerThread(ModularWorldServer modularWorldServer) {
    this.modularWorldServer = modularWorldServer;
    this.modularWorldServer.thread = this;
    setName(Side.Server.name());
  }

  @Override
  public void run() {
    running = true;
    try {
      modularWorldServer.create();

      while (running) {
        long l = System.currentTimeMillis() / ModularWorld.tickMS;
        modularWorldServer.render();
        while ((System.currentTimeMillis() / ModularWorld.tickMS) == l) { //Only run once every "ModularWorld.tickMS"
          try {
            sleep(1);
          } catch (Exception e) {
          }
        }
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
