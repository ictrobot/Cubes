package ethanjones.modularworld.graphics.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ethanjones.modularworld.input.InputChain;

public abstract class Menu {

  protected final Skin skin;
  protected final Stage stage;

  public Menu() {
    this.skin = MenuManager.instance.skin;
    stage = new Stage(new ScreenViewport());
  }

  public abstract void render();

  public void resize(int width, int height) {
    ((OrthographicCamera) stage.getViewport().getCamera()).setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
  }

  public void hide() {
    InputChain.getInputMultiplexer().addProcessor(0, stage);
  }

  public void show() {
    InputChain.getInputMultiplexer().removeProcessor(stage);
  }

  public void dispose() {
    stage.dispose();
  }
}
