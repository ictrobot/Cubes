package ethanjones.modularworld.core.platform.android;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import ethanjones.modularworld.core.Branding;
import ethanjones.modularworld.core.compatibility.Compatibility;
import ethanjones.modularworld.core.events.EventHandler;
import ethanjones.modularworld.core.events.setting.AfterProcessSettingEvent;
import ethanjones.modularworld.core.settings.Settings;
import ethanjones.modularworld.graphics.AssetManager;

public class AndroidCompatibility extends Compatibility {

  private AndroidLauncher androidLauncher;

  protected AndroidCompatibility(AndroidLauncher androidLauncher) {
    super(Application.ApplicationType.Android);
    this.androidLauncher = androidLauncher;
  }

  @EventHandler
  public void compatibilitySettings(AfterProcessSettingEvent event) {
    Settings.renderer_block_viewDistance.getIntegerSetting().setValue(0);
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
}
