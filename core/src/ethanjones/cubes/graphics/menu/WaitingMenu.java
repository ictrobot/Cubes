package ethanjones.cubes.graphics.menu;

import java.util.ArrayList;
import java.util.List;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.messaging.Message;
import ethanjones.cubes.core.messaging.MessageManager;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.side.ControlMessage;
import ethanjones.cubes.side.ControlMessage.Status;

public class WaitingMenu extends InfoMenu {

  public static interface Callback {

    public void callback();
  }

  private final List<Object> objects;
  private final Callback callback;

  public WaitingMenu(Callback callback) {
    super(Localization.get("menu.general.waiting"), false);
    this.callback = callback;
    this.objects = new ArrayList<Object>();
  }

  public void addObject(Object object) {
    objects.add(object);
  }

  @Override
  public void render() {
    super.render();
    if (objects.isEmpty()) {
      callback.callback();
      Adapter.setMenu(new MainMenu());
      Adapter.setServer(null);
      Adapter.setClient(null);
    }
    if (!MessageManager.hasMessages(this)) return;
    for (Message message : MessageManager.getMessages(this)) {
      if (message instanceof ControlMessage) {
        ControlMessage controlMessage = (ControlMessage) message;
        if (controlMessage.status == Status.Stopped) {
          if (controlMessage.from == null) continue;
          objects.remove(controlMessage.from);
        }
      }
    }
  }
}
