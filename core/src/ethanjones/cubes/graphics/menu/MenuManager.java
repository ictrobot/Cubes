package ethanjones.cubes.graphics.menu;

import com.badlogic.gdx.utils.Array;

public class MenuManager {

  private static Menu menu;
  private static Array<Menu> menus = new Array<Menu>();

  public static void setMenu(Menu menu) {
    int i = menus.indexOf(menu, true);
    if (i == -1) {
      menus.add(menu);
    } else if (i + 1 < menus.size) {
      menus.removeRange(i + 1, menus.size - 1);
    }
    MenuManager.menu = menu;
  }

  public static Menu getPrevious(Menu menu) {
    int index = menus.indexOf(menu, true);
    if (index == -1 || index == 0) return null;
    return menus.get(index - 1);
  }
}
