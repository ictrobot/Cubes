package ethanjones.cubes.graphics.menus;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.core.platform.StopLoopException;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.graphics.menu.MenuTools;

public class PauseMenu extends Menu {
  TextButton resume;
  TextButton quit;
  Image background;

  public PauseMenu() {
    resume = new TextButton(Localization.get("menu.pause.resume"), skin);
    resume.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        Adapter.setMenu(null);
      }
    });
    quit = new TextButton(Localization.get("menu.pause.quit"), skin);
    quit.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        try {
          Adapter.gotoMainMenu();
        } catch (StopLoopException ignored) {
        }
      }
    });
    background = new Image(Assets.getTextureRegion("core:hud/PauseBackground.png"));
    
    stage.addActor(background);
    stage.addActor(resume);
    stage.addActor(quit);
    //background.toBack();
  }

  @Override
  public void resize(float width, float height) {
    MenuTools.setMaxPrefSize(resume, quit);
    MenuTools.center(resume, quit);
    resume.moveBy(0, resume.getHeight() / 2);
    quit.moveBy(0, -quit.getHeight() / 2);
    background.setBounds(0, 0, width, height);
  }

  @Override
  public boolean shouldRenderBackground() {
    return false;
  }

  @Override
  public boolean blockClientInput() {
    return true;
  }
}
