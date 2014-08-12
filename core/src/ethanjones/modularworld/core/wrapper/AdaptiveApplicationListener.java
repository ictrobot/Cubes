package ethanjones.modularworld.core.wrapper;

import com.badlogic.gdx.ApplicationListener;

public class AdaptiveApplicationListener implements ApplicationListener {

  private ApplicationListener listener;

  public AdaptiveApplicationListener() {

  }

  public void setListener(ApplicationListener listener) {
    this.listener = listener;
  }

  public ApplicationListener getListener() {
    return listener;
  }


  @Override
  public void create() {
    if (listener != null) listener.create();
  }

  @Override
  public void resize(int width, int height) {
    if (listener != null) listener.resize(width, height);
  }

  @Override
  public void render() {
    if (listener != null) listener.render();
  }

  @Override
  public void pause() {
    if (listener != null) listener.pause();
  }

  @Override
  public void resume() {
    if (listener != null) listener.resume();
  }

  @Override
  public void dispose() {
    if (listener != null) listener.dispose();
  }
}
