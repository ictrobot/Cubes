package ethanjones.cubes.core.platform.html;

import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.platform.Launcher;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;

public class HtmlCompatibility extends Compatibility {
  ApplicationListener applicationListener;
  
  HtmlCompatibility(Launcher launcher) {
    super(launcher, ApplicationType.WebGL);
  }
  
  @Override
  protected void run(ApplicationListener applicationListener) {
    this.applicationListener = applicationListener;
  }
  
  @Override
  public int getFreeMemory() {
    return 0;
  }
  
  @Override
  public boolean functionModifier() {
    return false;
  }
}
