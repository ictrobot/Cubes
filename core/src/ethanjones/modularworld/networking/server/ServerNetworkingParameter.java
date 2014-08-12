package ethanjones.modularworld.networking.server;

import ethanjones.modularworld.core.settings.Settings;

public class ServerNetworkingParameter {
  public int port;

  public ServerNetworkingParameter() {
    this(Settings.networking_port.getIntegerSetting().getValue());
  }

  public ServerNetworkingParameter(int port) {
    this.port = port;
  }
}
