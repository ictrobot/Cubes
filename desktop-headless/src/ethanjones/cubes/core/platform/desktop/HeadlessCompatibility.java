package ethanjones.cubes.core.platform.desktop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.files.FileHandle;

public class HeadlessCompatibility extends DesktopCompatibility {

  protected HeadlessCompatibility(HeadlessLauncher headlessLauncher, String[] arg) {
    super(headlessLauncher, Application.ApplicationType.HeadlessDesktop, arg);
  }

  public boolean isHeadless() {
    return true;
  }

  @Override
  protected void run(ApplicationListener applicationListener) {
    new HeadlessApplication(applicationListener);
  }

  @Override
  public FileHandle getBaseFolder() {
    return getWorkingFolder();
  }
}
