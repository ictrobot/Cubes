package ethanjones.modularworld.core.platform.desktop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.files.FileHandle;

public class HeadlessDesktopCompatibility extends DesktopCompatibility {

  protected HeadlessDesktopCompatibility() {
    super(Application.ApplicationType.HeadlessDesktop);
  }

  public boolean isHeadless() {
    return true;
  }

  @Override
  public FileHandle getBaseFolder() {
    return getWorkingFolder();
  }
}
