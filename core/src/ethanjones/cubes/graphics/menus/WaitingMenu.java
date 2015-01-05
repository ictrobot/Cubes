package ethanjones.cubes.graphics.menus;

import ethanjones.cubes.core.localization.Localization;

public class WaitingMenu extends InfoMenu {

  private final Runnable runnable;

  public WaitingMenu(Runnable runnable) {
    super(Localization.get("menu.general.waiting"), false);
    this.runnable = runnable;
  }

  @Override
  public void render() {
    super.render();
    runnable.run();
  }
}
