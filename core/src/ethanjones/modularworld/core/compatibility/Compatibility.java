package ethanjones.modularworld.core.compatibility;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.core.ModularWorldException;

public abstract class Compatibility {

  public final Application.ApplicationType applicationType;

  protected Compatibility(Application.ApplicationType applicationType) {
    this.applicationType = applicationType;
    ModularWorld.instance.eventBus.register(this);
  }

  public static Compatibility getCompatibility() {
    switch (Gdx.app.getType()) {
      case Android:
        return new AndroidCompatibility();
      case Desktop:
        return new DesktopCompatibility();
      case HeadlessDesktop:
        return new HeadlessDesktopCompatibility();
      default:
        throw new ModularWorldException("No Compatibility module for this platform: " + Gdx.app.getType().name());
    }
  }

  public boolean isHeadless() {
    return false;
  }

}
