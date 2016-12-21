package ethanjones.cubes.graphics.menus;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.platform.Adapter;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class ConnectionFailedMenu extends InfoMenu {

  private boolean firstRender = true;

  public ConnectionFailedMenu(Exception e) {
    super(Localization.get("menu.connection_failed.failed_exception") + getString(e), Localization.get("menu.general.return_main_menu"));
  }

  private static String getString(Exception e) {
    if (e.getCause() == null) return "\n" + e.getClass().getSimpleName();
    return "\n" + e.getClass().getSimpleName() + "\n" + e.getCause();
  }

  public ConnectionFailedMenu() {
    super(Localization.get("menu.connection_failed.failed"), Localization.get("menu.general.return_main_menu"));
  }

  @Override
  public void render() {
    super.render();
    if (!firstRender) return;
    addButtonListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof ChangeListener.ChangeEvent)) return false;
        Adapter.setMenu(new MainMenu());
        return true;
      }
    });
    firstRender = false;
  }
  
  public static class DisconnectedMenu extends ConnectionFailedMenu {
    
    public DisconnectedMenu(Exception e) {
      super(e);
      text.setText(Localization.get("menu.disconnected.disconnected_exception") + getString(e));
    }
  
    public DisconnectedMenu() {
      super();
      text.setText(Localization.get("menu.disconnected.disconnected"));
    }
    
  }
}
