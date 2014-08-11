package ethanjones.modularworld.core.platform.desktop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.files.FileHandle;
import ethanjones.modularworld.networking.NetworkingManager;

public class HeadlessCompatibility extends DesktopCompatibility {

  protected HeadlessCompatibility(String[] arg) {
    super(Application.ApplicationType.HeadlessDesktop, arg);
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

  @Override
  public void setNetworkParameter() {
    NetworkingManager.NETWORK_PARAMETER = NetworkingManager.NETWORK_PARAMETER_SERVER;
  }
}
