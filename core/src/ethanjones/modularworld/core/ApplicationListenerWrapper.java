package ethanjones.modularworld.core;

import com.badlogic.gdx.ApplicationListener;
import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.core.logging.Log;

public class ApplicationListenerWrapper implements ApplicationListener {

  public final ModularWorld modularWorld;

  public ApplicationListenerWrapper(ModularWorld modularWorld) {
    this.modularWorld = modularWorld;
  }

  @Override
  public void create() {
    try {
      modularWorld.create();
    } catch (Exception e) {
      Log.error(Branding.NAME, "", ModularWorldException.getModularWorldException(e));
    }
  }

  @Override
  public void resize(int width, int height) {
    try {
      modularWorld.resize(width, height);
    } catch (Exception e) {
      Log.error(Branding.NAME, "", ModularWorldException.getModularWorldException(e));
    }
  }

  @Override
  public void render() {
    try {
      modularWorld.render();
    } catch (Exception e) {
      Log.error(Branding.NAME, "", ModularWorldException.getModularWorldException(e));
    }
  }

  @Override
  public void pause() {
    try {
      modularWorld.pause();
    } catch (Exception e) {
      Log.error(Branding.NAME, "", ModularWorldException.getModularWorldException(e));
    }
  }

  @Override
  public void resume() {
    try {
      modularWorld.resume();
    } catch (Exception e) {
      Log.error(Branding.NAME, "", ModularWorldException.getModularWorldException(e));
    }
  }

  @Override
  public void dispose() {
    try {
      modularWorld.dispose();
    } catch (Exception e) {
      Log.error(Branding.NAME, "", ModularWorldException.getModularWorldException(e));
    }
  }
}
