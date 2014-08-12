package ethanjones.modularworld.graphics.menu;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

public class MenuAdapter extends ApplicationAdapter {

  private Menu menu;

  public void setMenu(Menu menu) {
    Menu old = menu;
    old.hide();
    old.dispose();
    this.menu = menu;
    menu.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    menu.show();
  }

  public Menu getMenu() {
    return menu;
  }

  @Override
  public void resize(int width, int height) {
    menu.resize(width, height);
  }

  public void render() {
    menu.render();
  }

  @Override
  public void dispose() {
    menu.dispose();
  }
}
