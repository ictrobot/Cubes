package ethanjones.modularworld.core;

import com.badlogic.gdx.ApplicationListener;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.networking.NetworkingManager;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.side.common.ModularWorld;
import ethanjones.modularworld.side.server.ModularWorldServer;

public class ModularWorldWrapper implements ApplicationListener {

  ModularWorldClient modularWorldClient;
  ModularWorldServer modularWorldServer;

  public ModularWorldWrapper() {
    if (NetworkingManager.isServerOnly() || !NetworkingManager.hasAddressToConnectTo()) {
      modularWorldServer = new ModularWorldServer();
    }
    if (!NetworkingManager.isServerOnly() || NetworkingManager.hasAddressToConnectTo()) {
      modularWorldClient = new ModularWorldClient();
    }
  }

  @Override
  public void create() {
    try {
      ModularWorld.setup();
      if (modularWorldServer != null) modularWorldServer.create();
      if (modularWorldClient != null) modularWorldClient.create();
    } catch (Exception e) {
      Log.error(Branding.NAME, "", ModularWorldException.getModularWorldException(e));
    }
  }

  @Override
  public void resize(int width, int height) {
    try {
      if (modularWorldServer != null) modularWorldServer.resize(width, height);
      if (modularWorldClient != null) modularWorldClient.resize(width, height);
    } catch (Exception e) {
      Log.error(Branding.NAME, "", ModularWorldException.getModularWorldException(e));
    }
  }

  @Override
  public void render() {
    try {
      if (modularWorldServer != null) modularWorldServer.render();
      if (modularWorldClient != null) modularWorldClient.render();
    } catch (Exception e) {
      Log.error(Branding.NAME, "", ModularWorldException.getModularWorldException(e));
    }
  }

  @Override
  public void pause() {
    try {
      if (modularWorldServer != null) modularWorldServer.pause();
      if (modularWorldClient != null) modularWorldClient.pause();
    } catch (Exception e) {
      Log.error(Branding.NAME, "", ModularWorldException.getModularWorldException(e));
    }
  }

  @Override
  public void resume() {
    try {
      if (modularWorldServer != null) modularWorldServer.resume();
      if (modularWorldClient != null) modularWorldClient.resume();
    } catch (Exception e) {
      Log.error(Branding.NAME, "", ModularWorldException.getModularWorldException(e));
    }
  }

  @Override
  public void dispose() {
    try {
      if (modularWorldServer != null) modularWorldServer.dispose();
      if (modularWorldClient != null) modularWorldClient.dispose();
    } catch (Exception e) {
      Log.error(Branding.NAME, "", ModularWorldException.getModularWorldException(e));
    }
  }
}
