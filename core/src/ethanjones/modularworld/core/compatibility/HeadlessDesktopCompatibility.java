package ethanjones.modularworld.core.compatibility;

import com.badlogic.gdx.Application;

public class HeadlessDesktopCompatibility extends DesktopCompatibility {

  protected HeadlessDesktopCompatibility() {
    super(Application.ApplicationType.HeadlessDesktop);
  }

  public boolean isHeadless() {
    return true;
  }

}
