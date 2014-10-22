package ethanjones.modularworld.core.adapter;

import com.badlogic.gdx.ApplicationListener;
import ethanjones.modularworld.core.system.Debug;
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
    try {
      modularWorldServer.resize(width, height);
    } catch (Exception e) {
      Debug.crash(e);
    }
  }

  @Override
  public void render() {
    try {
      ModularWorld.staticRender();
      modularWorldServer.render();
    } catch (Exception e) {
      Debug.crash(e);
    }
  }

  @Override
  public void pause() {
    try {
      modularWorldServer.pause();
    } catch (Exception e) {
      Debug.crash(e);
    }
  }

  @Override
  public void resume() {
    try {
      modularWorldServer.resume();
    } catch (Exception e) {
      Debug.crash(e);
    }
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
