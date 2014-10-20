package ethanjones.modularworld.graphics.menu.menus;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import ethanjones.modularworld.core.adapter.GraphicalAdapter;
import ethanjones.modularworld.core.localization.Localization;
import ethanjones.modularworld.graphics.menu.Menu;
import ethanjones.modularworld.graphics.menu.MenuTools;

public class ServerSetupMenu extends Menu {

  Label title;
  TextField port;
  TextButton start;
  TextButton back;

  public ServerSetupMenu() {
    super();
    title = new Label(Localization.get("menu.server.title"), skin.get("title", Label.LabelStyle.class));
    port = new TextField("", skin);
    port.setMessageText(Localization.get("menu.server.port"));
    port.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
    start = new TextButton(Localization.get("menu.server.start"), skin);
    back = MenuTools.getBackButton(this);

    start.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof ChangeListener.ChangeEvent)) return false;
        GraphicalAdapter.instance.setMenu(new ServerRunningMenu(port.getText().isEmpty() ? 8080 : Integer.parseInt(port.getText())));
        return true;
      }
    });
  }

  @Override
  public void addActors() {
    stage.addActor(title);
    stage.addActor(port);
    stage.addActor(start);
    stage.addActor(back);
  }

  @Override
  public void resize(int width, int height) {
    super.resize(width, height);
    MenuTools.setTitle(title);
    MenuTools.arrange(width / 4, height / 2, width / 2, height / 4, MenuTools.Direction.Above, start, port);
    MenuTools.copyPosAndSize(start, back);
    back.setY(0);
  }
}
