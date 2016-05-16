package ethanjones.cubes.networking.packet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public enum PacketPriority {
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
