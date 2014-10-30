package ethanjones.cubes.networking.server;

import ethanjones.cubes.core.settings.Settings;

public class ServerNetworkingParameter {

  public int port;

  public ServerNetworkingParameter() {
    this(Settings.getIntegerSettingValue(Settings.NETWORKING_PORT));
  }

  public ServerNetworkingParameter(int port) {
    this.port = port;
  }
}
