package ethanjones.modularworld.core.adapter;

import com.badlogic.gdx.ApplicationListener;
import ethanjones.modularworld.core.ModularWorldException;
import ethanjones.modularworld.core.logging.Log;
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
      Log.error(ModularWorldException.getModularWorldException(e));
    }
  }

  @Override
  public void resize(int width, int height) {
    try {
      if (modularWorldServer != null) modularWorldServer.resize(width, height);
    } catch (Exception e) {
      Log.error(ModularWorldException.getModularWorldException(e));
    }
  }

  @Override
  public void render() {
    try {
      ModularWorld.staticRender();
      if (modularWorldServer != null) modularWorldServer.render();
    } catch (Exception e) {
      Log.error(ModularWorldException.getModularWorldException(e));
    }
  }

  @Override
  public void pause() {
    try {
      if (modularWorldServer != null) modularWorldServer.pause();
    } catch (Exception e) {
      Log.error(ModularWorldException.getModularWorldException(e));
    }
  }

  @Override
  public void resume() {
    try {
      if (modularWorldServer != null) modularWorldServer.resume();
    } catch (Exception e) {
      Log.error(ModularWorldException.getModularWorldException(e));
    }
  }

  @Override
  public void dispose() {
    try {
      if (modularWorldServer != null) modularWorldServer.dispose();
    } catch (Exception e) {
      Log.error(ModularWorldException.getModularWorldException(e));
    }
  }
}
