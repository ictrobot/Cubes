package ethanjones.cubes.core.platform.android;

import android.os.Build;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;

import ethanjones.cubes.core.adapter.GraphicalAdapter;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.mod.ModLoader;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.system.Branding;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.graphics.menu.MenuManager;
import ethanjones.cubes.graphics.menu.menus.MainMenu;

public class AndroidCompatibility extends Compatibility {

  public AndroidLauncher androidLauncher;
  private AndroidModLoader modLoader;
  protected boolean back = false;

  protected AndroidCompatibility(AndroidLauncher androidLauncher) {
    super(androidLauncher, Application.ApplicationType.Android);
    this.androidLauncher = androidLauncher;
    modLoader = new AndroidModLoader(this);
  }

  @Override
  public FileHandle getBaseFolder() {
    return Gdx.files.external(Branding.NAME);
  }

  @Override
  public FileHandle getWorkingFolder() {
    return Gdx.files.internal(".");
  }

  @Override
  public void setupAssets() {
    super.setupAssets();
  }

  @Override
  public void logEnvironment() {
    Log.debug("Android Version:    " + Build.VERSION.RELEASE);
    Log.debug("Android SDK:        " + Build.VERSION.SDK_INT);
    Log.debug("Brand:              " + Build.BRAND);
    Log.debug("Model:              " + Build.MODEL);
    Log.debug("Product:            " + Build.PRODUCT);
  }

  @Override
  public boolean isTouchScreen() {
    return true;
  }

  @Override
  protected void run(ApplicationListener applicationListener) {
    AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
    androidLauncher.initialize(applicationListener, config);
  }

  @Override
  public ModLoader getModLoader() {
    return modLoader;
  }

  @Override
  public void render() {
    if (back) {
      back = false;
      Menu current = GraphicalAdapter.instance.getMenu();
      if (current == null) {
        GraphicalAdapter.instance.setMenu(new MainMenu());
        GraphicalAdapter.instance.setCubes(null, null);
        return;
      }
      Menu prev = MenuManager.getPrevious(current);
      if (prev == null) return;
      GraphicalAdapter.instance.setMenu(prev);
    }
  }
}
