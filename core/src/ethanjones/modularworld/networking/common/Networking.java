package ethanjones.modularworld.networking.common;

import ethanjones.modularworld.networking.common.packet.PacketHandler;

public abstract class Networking implements PacketHandler {

  public final int port;

  public Networking(int port) {
    this.port = port;
  }

  public abstract void start();

  public abstract void stop();

}
