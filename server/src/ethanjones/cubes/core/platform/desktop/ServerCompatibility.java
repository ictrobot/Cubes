package ethanjones.cubes.core.platform.desktop;

import ethanjones.cubes.core.system.Branding;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;

public class ServerCompatibility extends DesktopCompatibility {

  ServerCompatibility(ServerLauncher serverLauncher, String[] arg) {
    super(serverLauncher, Application.ApplicationType.HeadlessDesktop, arg);
  }

  @Override
  protected void run(ApplicationListener applicationListener) {
    HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
    config.renderInterval = -1; // internal loop called from within create()
    
    new HeadlessApplication(applicationListener, config);
  }

  @Override
  public FileHandle getBaseFolder() {
    if (baseFolder != null) return baseFolder;
    return Branding.IS_DEBUG ? workingFolder.child("server") : workingFolder;
  }
}
