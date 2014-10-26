package ethanjones.modularworld.networking.server;

import ethanjones.modularworld.core.settings.Settings;

public class ServerNetworkingParameter {
  public int port;

  public ServerNetworkingParameter() {
    this(Settings.getIntegerSettingValue(Settings.NETWORKING_PORT));
  }

  public ServerNetworkingParameter(int port) {
    this.port = port;
  }
}
