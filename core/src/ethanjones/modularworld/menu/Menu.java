package ethanjones.modularworld.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public abstract class Menu implements Screen {

  protected final MenuManager menuManager;
  protected final Skin skin;
  protected final Stage stage;

  public Menu(MenuManager menuManager) {
    this.menuManager = menuManager;
    this.skin = menuManager.skin;
    stage = new Stage(new ScreenViewport());
    Gdx.input.setInputProcessor(stage); //Move to input class
  }

  @Override
  public void resize(int width, int height) {
    Camera camera = stage.getViewport().getCamera();
    camera.viewportWidth = width;
    camera.viewportHeight = height;
    camera.update();
  }

  @Override
  public void dispose() {
    stage.dispose();
  }
}
