package ethanjones.modularworld.core.compatibility;

import com.badlogic.gdx.Application;

public class DesktopCompatibility extends Compatibility {

  protected DesktopCompatibility() {
    this(Application.ApplicationType.Desktop);
  }

  protected DesktopCompatibility(Application.ApplicationType applicationType) {
    super(applicationType);
  }

}
