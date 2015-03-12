package ethanjones.cubes.common.networking.server;

import ethanjones.cubes.common.core.settings.Settings;

public class ServerNetworkingParameter {

  public int port;

  public ServerNetworkingParameter() {
    this(Settings.getIntegerSettingValue(Settings.NETWORKING_PORT));
  }

  public ServerNetworkingParameter(int port) {
    this.port = port;
  }
}
