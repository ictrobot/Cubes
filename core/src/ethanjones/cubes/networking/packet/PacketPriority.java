package ethanjones.cubes.networking.packet;

import com.badlogic.gdx.utils.reflect.Annotation;
import com.badlogic.gdx.utils.reflect.ClassReflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public enum PacketPriority {
  HIGH, MEDIUM, LOW;

  public static PacketPriority get(Class<? extends Packet> packet) {
    Annotation ann = ClassReflection.getAnnotation(packet.getClass(), Priority.class);
    if (ann == null) return MEDIUM;
    Priority annotation = ann.getAnnotation(Priority.class);
    if (annotation == null) return MEDIUM;
    return annotation.value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  public @interface Priority {
    PacketPriority value();
  }
}
