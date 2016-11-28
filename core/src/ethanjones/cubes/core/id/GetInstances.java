package ethanjones.cubes.core.id;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.item.Item;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class GetInstances {
  
  public static void get(Class<?> c) {
    for (java.lang.reflect.Field field : c.getDeclaredFields()) {
      if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
        if (field.isAnnotationPresent(GetBlock.class)) {
          GetBlock g = field.getAnnotation(GetBlock.class);
          Block block = IDManager.toBlock(g.value());
          try {
            field.set(null, block);
          } catch (IllegalAccessException e) {
            throw new CubesException("Failed to GetInstances", e);
          }
        } else if (field.isAnnotationPresent(GetItem.class)) {
          GetItem g = field.getAnnotation(GetItem.class);
          Item item = IDManager.toItem(g.value());
          try {
            field.set(null, item);
          } catch (IllegalAccessException e) {
            throw new CubesException("Failed to GetInstances", e);
          }
        }
      }
    }
  }
  
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface GetBlock {
    String value();
  }
  
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface GetItem {
    String value();
  }
}
