package ethanjones.modularworld.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public abstract class Menu implements Screen {

  protected final Skin skin;
  protected final Stage stage;

  public Menu() {
    this.skin = MenuManager.instance.skin;
    stage = new Stage(new ScreenViewport());
    Gdx.input.setInputProcessor(stage); //TODO Move to input class
  }

  @Override
  public void resize(int width, int height) {
    ((OrthographicCamera) stage.getViewport().getCamera()).setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
  }

  @Override
  public void dispose() {
    stage.dispose();
  }
}
