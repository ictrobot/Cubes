package ethanjones.modularworld.core.platform.desktop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.files.FileHandle;

public class HeadlessCompatibility extends DesktopCompatibility {

  protected HeadlessCompatibility(String[] arg) {
    super(Application.ApplicationType.Desktop, arg);
  }

  public boolean isHeadless() {
    return true;
  }

  @Override
  public FileHandle getBaseFolder() {
    return getWorkingFolder();
  }

  @Override
  protected void run(ApplicationListener applicationListener) {
    new HeadlessApplication(applicationListener);
  }
}
