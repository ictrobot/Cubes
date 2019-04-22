package ethanjones.cubes.graphics.menus;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.system.Branding;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.menu.Fonts;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.world.storage.WorldStorage;

import static ethanjones.cubes.graphics.Graphics.GUI_HEIGHT;
import static ethanjones.cubes.graphics.Graphics.GUI_WIDTH;

public class MainMenu extends Menu {

  private static Value cellHeight = new Value() {
    @Override
    public float get(Actor context) {
      return GUI_HEIGHT / 8;
    }
  };
  private static Value cellWidth = new Value() {
    @Override
    public float get(Actor context) {
      return GUI_WIDTH / 2;
    }
  };
  Image logo;
  Label version;
  Label author;
  Table buttons;
  TextButton singleplayer;
  TextButton settings;
  TextButton quit;

  public MainMenu() {
    super();
    logo = new Image(new TextureRegionDrawable(Assets.getTextureRegion("core:logo.png")), Scaling.fillY, Align.center);
    version = new Label(Branding.DEBUG + "\nForked from Cubes 0.0.5.1055", new Label.LabelStyle(Fonts.smallHUD, Color.WHITE));
    author = new Label(Branding.AUTHOR, new Label.LabelStyle(Fonts.smallHUD, Color.WHITE));
    buttons = new Table();
    buttons.defaults().height(cellHeight).width(cellWidth).pad(4).fillX().fillY();
    buttons.add(singleplayer = new TextButton(Localization.get("menu.main.singleplayer"), skin)).row();
    singleplayer.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        if (WorldStorage.getInterface() != null) {
          Adapter.setMenu(DisclaimerMenu.getDisclaimer(new SingleplayerSavesMenu()));
        } else {
          Adapter.setMenu(DisclaimerMenu.getDisclaimer(new SingleplayerMenu()));
        }
      }
    });
    buttons.add(settings = new TextButton(Localization.get("menu.main.settings"), skin)).row();
    settings.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        Adapter.setMenu(new SettingsMenu());
      }
    });
    if (Compatibility.get().getApplicationType() == ApplicationType.WebGL) {
      TextButton download, source;
      buttons.add().height(20f).row();
      buttons.add(download = new TextButton(Localization.get("menu.main.downloadCubes"), skin)).row();
      download.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
          Compatibility.get().openURL("https://cubes.ethanjones.me/");
        }
      });
      buttons.add(source = new TextButton(Localization.get("menu.main.sourceCode"), skin)).row();
      source.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
          Compatibility.get().openURL("https://github.com/ictrobot/cubes/");
        }
      });
    } else {
      buttons.add(quit = new TextButton(Localization.get("menu.main.quit"), skin)).row();
      quit.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
          Log.debug("Quit pressed");
          Adapter.quit();
        }
      });
    }

    stage.addActor(logo);
    stage.addActor(version);
    stage.addActor(author);
    stage.addActor(buttons);
  }

  @Override
  public void resize(float width, float height) {
    super.resize(width, height);
    logo.setBounds(0, (height / 4 * 3) + 2, width, height / 6);
    version.setBounds(2, 2, version.getPrefWidth(), version.getPrefHeight());
    version.setAlignment(Align.left);
    author.setBounds(width - author.getPrefWidth() - 2, 2, author.getPrefWidth(), author.getPrefHeight());
    author.setAlignment(Align.right);
    buttons.setBounds(0, 0, width, (height * 3 / 4) - 22);
    buttons.align(Align.top);
    buttons.layout();
  }
}
