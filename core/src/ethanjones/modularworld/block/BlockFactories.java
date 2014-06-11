package ethanjones.modularworld.block;

import java.lang.reflect.Field;
import java.util.ArrayList;
import ethanjones.modularworld.block.basic.BlockFactoryStone;
import ethanjones.modularworld.core.util.Register;

public class BlockFactories {
  @Register
  public static BlockFactoryStone stone;
  
  private static ArrayList<BlockFactory> factories = new ArrayList<BlockFactory>();

  public static void init() {
    for (Field f : BlockFactories.class.getFields()) {
      try {
        if (f.isAnnotationPresent(Register.class)) {
          Object o = f.getType().newInstance();
          if (o instanceof BlockFactory) {
            f.set(null, o);
            factories.add((BlockFactory) o);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  public static void loadTextures() {
    for (BlockFactory f : factories) {
      f.loadTextures();
    }
  }
}
