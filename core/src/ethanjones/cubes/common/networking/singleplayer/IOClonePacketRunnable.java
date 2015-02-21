package ethanjones.cubes.common.networking.singleplayer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import ethanjones.cubes.common.Debug;
import ethanjones.cubes.common.networking.packet.Packet;
import ethanjones.cubes.common.networking.packet.PacketQueue;
import ethanjones.cubes.common.Side;
import ethanjones.cubes.common.Sided;

public class IOClonePacketRunnable implements Runnable {

  public final Packet packet;
  public final PacketQueue packetQueue;

  public IOClonePacketRunnable(Packet packet, PacketQueue packetQueue) {
    this.packet = packet;
    this.packetQueue = packetQueue;
  }

  @Override
  public void run() {
    try {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      packet.write(new DataOutputStream(byteArrayOutputStream));
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
      if (Sided.getSide() == Side.Server) { //Switch side
        Sided.setSide(Side.Client);
      } else {
        Sided.setSide(Side.Server);
      }
      Packet n = packet.getClass().newInstance();
      n.read(new DataInputStream(byteArrayInputStream));
      packetQueue.add(n);
    } catch (Exception e) {
      Debug.crash(e);
    }
  }
}
