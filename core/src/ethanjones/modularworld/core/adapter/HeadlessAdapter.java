package ethanjones.modularworld.core.adapter;

import com.badlogic.gdx.ApplicationListener;
import ethanjones.modularworld.core.system.Debug;
import ethanjones.modularworld.core.system.Memory;
import ethanjones.modularworld.networking.server.ServerNetworkingParameter;
import ethanjones.modularworld.side.common.ModularWorld;
import ethanjones.modularworld.side.server.ModularWorldServer;

public class HeadlessAdapter implements ApplicationListener {

  ModularWorldServer modularWorldServer;

  @Override
  public void create() {
    try {
      Thread.currentThread().setName("MAIN");
      ModularWorld.setup();
      modularWorldServer = new ModularWorldServer(new ServerNetworkingParameter());
      modularWorldServer.create();
    } catch (Exception e) {
      Debug.crash(e);
    }
  }

  @Override
  public void resize(int width, int height) {

  }

  @Override
  public void render() {
    try {
      Memory.update();
      modularWorldServer.render();
    } catch (Exception e) {
      Debug.crash(e);
    }
  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  @Override
  public void dispose() {
    try {
      modularWorldServer.dispose();
    } catch (Exception e) {
      Debug.crash(e);
    }
  }
}
