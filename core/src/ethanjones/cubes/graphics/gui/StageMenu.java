package ethanjones.cubes.graphics.gui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ethanjones.cubes.input.InputChain;

public abstract class StageMenu implements Menu {

  private static ScreenViewport viewport = new ScreenViewport();
  protected Stage stage = new Stage(viewport, Gui.batch);

  public void resize(int width, int height) {
    viewport.update(width, height, true);
  }

  public void render() {
    stage.act();
    stage.draw();
  }

  public void hide() {
    InputChain.getInputMultiplexer().removeProcessor(stage);
  }

  public void show() {
    InputChain.getInputMultiplexer().addProcessor(0, stage);
  }
}
