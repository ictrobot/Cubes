package ethanjones.cubes.graphics.menus;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.graphics.menu.MenuManager;

public class DisclaimerMenu extends InfoMenu {

  private static boolean shown = false;

  private DisclaimerMenu(final Menu next) {
    super(Localization.get("client.gwtMessage"), Localization.get("client.gwtOK"));

    addButtonListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        Adapter.setMenu(next);
        MenuManager.removePrevious(DisclaimerMenu.this);
      }
    });
  }

  @Override
  public void show() {
    Log.info("Showing Cubes Minimized Disclaimer");
  }

  public static Menu getDisclaimer(Menu next) {
    if (!shown) {
      next = new DisclaimerMenu(next);
      shown = true;
    }
    return next;
  }
}
