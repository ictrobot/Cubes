package ethanjones.cubes.graphics.menu.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

import ethanjones.cubes.core.adapter.GraphicalAdapter;
import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.side.common.Cubes;

public class MainMenu extends Menu {

  private static Value cellHeight = new Value() {
    @Override
    public float get(Actor context) {
      return Gdx.graphics.getHeight() / 8;
    }
  };
  private static Value cellWidth = new Value() {
    @Override
    public float get(Actor context) {
      return Gdx.graphics.getWidth() / 6 * 4;
    }
  };
  Image logo;
  Table buttons;
  TextButton singleplayer;
  TextButton multiplayer;
  TextButton serveronly;
  TextButton settings;
  TextButton quit;

  public MainMenu() {
    super();
    logo = new Image(new TextureRegionDrawable(Assets.getTextureRegion("core:logo.png")), Scaling.fillY, Align.center);
    buttons = new Table();
    buttons.defaults().height(cellHeight).width(cellWidth).pad(5).fillX().fillY();
    buttons.add(singleplayer = new TextButton(Localization.get("menu.main.singleplayer"), skin)).row();
    singleplayer.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof ChangeListener.ChangeEvent)) return false;
        GraphicalAdapter.instance.setMenu(new SingleplayerLoadingMenu());
        return true;
      }
    });
    buttons.add(multiplayer = new TextButton(Localization.get("menu.main.multiplayer"), skin)).row();
    multiplayer.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof ChangeListener.ChangeEvent)) return false;
        GraphicalAdapter.instance.setMenu(new MultiplayerConnectMenu());
        return true;
      }
    });
    buttons.add(serveronly = new TextButton(Localization.get("menu.main.serveronly"), skin)).row();
    serveronly.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof ChangeListener.ChangeEvent)) return false;
        GraphicalAdapter.instance.setMenu(new ServerSetupMenu());
        return true;
      }
    });
    buttons.add(settings = new TextButton(Localization.get("menu.main.settings"), skin)).row();
    settings.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof ChangeListener.ChangeEvent)) return false;
        GraphicalAdapter.instance.setMenu(new SettingsMenu());
        return true;
      }
    });
    buttons.add(quit = new TextButton(Localization.get("menu.main.quit"), skin)).row();
    quit.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof ChangeListener.ChangeEvent)) return false;
        Log.debug("Quit pressed");
        Cubes.quit(true);
        return true;
      }
    });
  }

  @Override
  public void resize(int width, int height) {
    super.resize(width, height);
    logo.setBounds(0, Gdx.graphics.getHeight() / 6 * 5, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 6);
    buttons.setBounds(0, 0, width, height / 6 * 5);
    buttons.align(Align.top);
    buttons.layout();
  }

  @Override
  public void addActors() {
    stage.addActor(logo);
    stage.addActor(buttons);
  }
}
