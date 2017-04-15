package ethanjones.cubes.client;

import ethanjones.cubes.core.platform.ClientAdapter;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

public class HtmlLauncher extends GwtApplication {
  
  @Override
  public GwtApplicationConfiguration getConfig() {
    return new GwtApplicationConfiguration(480, 320);
  }
  
  @Override
  public ApplicationListener createApplicationListener() {
    return new ClientAdapter();
  }
}