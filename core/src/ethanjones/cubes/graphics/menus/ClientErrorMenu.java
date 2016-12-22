package ethanjones.cubes.graphics.menus;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.platform.Adapter;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class ClientErrorMenu extends InfoMenu {

  private boolean firstRender = true;

  public ClientErrorMenu(String message, Exception e) {
    super(Localization.get(message) + getString(e), Localization.get("menu.general.return_main_menu"));
  }

  public ClientErrorMenu(String message) {
    super(Localization.get(message), Localization.get("menu.general.return_main_menu"));
  }
  
  private static String getString(Exception e) {
    if (e.getCause() == null) return "\n" + e.getClass().getSimpleName();
    return "\n" + e.getClass().getSimpleName() + "\n" + e.getCause();
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
  
  public static class ConnectionFailedMenu extends ClientErrorMenu {
    public ConnectionFailedMenu(Exception e) {
      super("menu.connection_failed.exception_message", e);
    }
    
    public ConnectionFailedMenu() {
      super("menu.connection_failed.message");
    }
  }
  
  public static class DisconnectedMenu extends ClientErrorMenu {
    public DisconnectedMenu(Exception e) {
      super("menu.disconnected.exception_message", e);
    }
  
    public DisconnectedMenu() {
      super("menu.disconnected.message");
    }
  }
  
  public static class UnresponsiveIntegratedServerMenu extends ClientErrorMenu {
    public UnresponsiveIntegratedServerMenu() {
      super("menu.unresponsive_integrated_server.message");
    }
  }
  
  public static class FailedStartingSingleplayerMenu extends ClientErrorMenu {
    public FailedStartingSingleplayerMenu(Exception e) {
      super("menu.failed_starting_singleplayer.exception_message", e);
    }
  }
  
  public static class FailedStartingServerMenu extends ClientErrorMenu {
    public FailedStartingServerMenu(Exception e) {
      super("menu.failed_starting_server.exception_message", e);
    }
  }
}
