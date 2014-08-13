package ethanjones.modularworld.core.platform.android;

import android.os.Build;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import ethanjones.modularworld.core.Branding;
import ethanjones.modularworld.core.compatibility.Compatibility;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.graphics.asset.AssetManager;

import java.lang.reflect.Field;

public class AndroidCompatibility extends Compatibility {

  private AndroidLauncher androidLauncher;

  protected AndroidCompatibility(AndroidLauncher androidLauncher) {
    super(Application.ApplicationType.Android);
    this.androidLauncher = androidLauncher;
  }

  @Override
  public void logEnvironment() {
    Log.debug("Properties", "Android Version:    " + Build.VERSION.RELEASE);
    Log.debug("Properties", "Android SDK:        " + Build.VERSION.SDK_INT);
    for (Field field : Build.class.getFields()) {
      try {
        Log.debug("Properties", "BUILD-" + field.getName() + field.get(null).toString());
      } catch (IllegalAccessException e) {
        
      }
    }
    //TODO: Log other stuff from android.os.Build
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
  public void getAssets(AssetManager assetManager) {
    super.getAssets(assetManager);
  }

  @Override
  protected void run(ApplicationListener applicationListener) {
    AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
    androidLauncher.initialize(applicationListener, config);
  }
}
