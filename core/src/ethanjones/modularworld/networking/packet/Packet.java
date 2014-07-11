package ethanjones.modularworld.networking.packet;

import ethanjones.modularworld.core.data.ByteBase;
import ethanjones.modularworld.core.data.ByteData;
import ethanjones.modularworld.core.data.ByteMode;
import ethanjones.modularworld.networking.SocketMonitor;

public abstract class Packet {

  public final String id;

  public Packet(String id) {
    this.id = id;
  }

  public abstract ByteBase getData();

  public final ByteData getPacketData() {
    ByteData byteData = new ByteData(new ByteMode.Named(id));
    byteData.setBase("data", this.getData());
    return byteData;
  }

  public abstract void process(SocketMonitor socketMonitor);
}
