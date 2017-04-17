package ethanjones.cubes.core.platform.html;

import ethanjones.cubes.core.platform.Launcher;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

public class HtmlLauncher extends GwtApplication implements Launcher {
  private HtmlCompatibility compatibility;
  
  public HtmlLauncher() {
    compatibility = new HtmlCompatibility(this);
    compatibility.startCubes();
  }
  
  @Override
  public GwtApplicationConfiguration getConfig() {
    return new GwtApplicationConfiguration(480, 320);
  }
  
  @Override
  public ApplicationListener createApplicationListener() {
    return compatibility.applicationListener;
  }
}