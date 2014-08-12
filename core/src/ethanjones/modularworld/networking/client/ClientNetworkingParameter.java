package ethanjones.modularworld.networking.client;

import ethanjones.modularworld.core.settings.Settings;

public class ClientNetworkingParameter {
  public String host;
  public int port;

  public ClientNetworkingParameter(String host) {
    this(host, Settings.networking_port.getIntegerSetting().getValue());
  }

  public ClientNetworkingParameter(String host, int port) {
    this.host = host;
    this.port = port;
  }
}
