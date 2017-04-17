package ethanjones.cubes.networking.packet;

import ethanjones.cubes.side.common.Side;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public enum PacketDirection {
  TO_CLIENT,
  TO_SERVER,
  OMNIDIRECTIONAL;

  public static boolean checkPacketSend(Class<? extends Packet> packet, Side side) {
    return true;
  }

  public static boolean checkPacketReceive(Class<? extends Packet> packet, Side side) {
    return true;
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  public @interface Direction {
    PacketDirection value();
  }
}
