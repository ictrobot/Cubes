package ethanjones.cubes.networking.packet;

import ethanjones.cubes.side.common.Side;

import com.badlogic.gdx.utils.reflect.Annotation;
import com.badlogic.gdx.utils.reflect.ClassReflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public enum PacketDirection {
  TO_CLIENT,
  TO_SERVER,
  OMNIDIRECTIONAL;

  public static boolean checkPacketSend(Class<? extends Packet> packet, Side side) {
    Annotation ann = ClassReflection.getAnnotation(packet.getClass(), Direction.class);
    if (ann == null) return true;
    Direction annotation = ann.getAnnotation(Direction.class);
    if (annotation == null) return true;
    PacketDirection dir = annotation.value();
    if (dir == OMNIDIRECTIONAL || (side == Side.Client && dir == TO_SERVER) || (side == Side.Server && dir == TO_CLIENT)) {
      return true;
    }
    throw new IllegalArgumentException(side.name() + " cannot send packet " + packet.getName());
  }

  public static boolean checkPacketReceive(Class<? extends Packet> packet, Side side) {
    Annotation ann = ClassReflection.getAnnotation(packet.getClass(), Direction.class);
    if (ann == null) return true;
    Direction annotation = ann.getAnnotation(Direction.class);
    if (annotation == null) return true;
    PacketDirection dir = annotation.value();
    if (dir == OMNIDIRECTIONAL || (side == Side.Client && dir == TO_CLIENT) || (side == Side.Server && dir == TO_SERVER)) {
      return true;
    }
    throw new IllegalArgumentException(side.name() + " cannot receive packet " + packet.getName());
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  public @interface Direction {
    PacketDirection value();
  }
}
