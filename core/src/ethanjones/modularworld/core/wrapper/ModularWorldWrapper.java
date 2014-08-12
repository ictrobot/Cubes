package ethanjones.modularworld.core.wrapper;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import ethanjones.modularworld.core.Branding;
import ethanjones.modularworld.core.ModularWorldException;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.side.server.ModularWorldServer;

public class ModularWorldWrapper implements ApplicationListener {

  ModularWorldServer modularWorldServer;
  ModularWorldClient modularWorldClient;

  public ModularWorldWrapper(ModularWorldServer modularWorldServer, ModularWorldClient modularWorldClient) {
    this.modularWorldServer = modularWorldServer;
    this.modularWorldClient = modularWorldClient;

    if (modularWorldServer != null) modularWorldServer.create();
    if (modularWorldClient != null) modularWorldClient.create();
    if (modularWorldServer != null) modularWorldServer.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    if (modularWorldClient != null) modularWorldClient.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
  }

  @Override
  public void create() {

    try {
      if (modularWorldServer != null) modularWorldServer.create();
      if (modularWorldClient != null) modularWorldClient.create();
    } catch (Exception e) {
      Log.error(Branding.NAME, "", ModularWorldException.getModularWorldException(e));
    }
  }

  @Override
  public void resize(int width, int height) {

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
