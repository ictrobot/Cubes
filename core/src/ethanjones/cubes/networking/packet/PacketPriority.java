package ethanjones.cubes.networking.packet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public enum PacketPriority {
  // when the socket is first connected only CONNECTION_INITIALIZATION packets will be sent until the connection is marked as initialized
  CONNECTION_INITIALIZATION,

  HIGH, MEDIUM, LOW;

  public static PacketPriority get(Class<? extends Packet> packet) {
    Priority annotation = packet.getAnnotation(Priority.class);
    if (annotation == null) return MEDIUM;
    return annotation.value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  public static @interface Priority {
    public PacketPriority value();
  }
}
