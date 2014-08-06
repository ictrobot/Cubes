package ethanjones.modularworld.block.factory;

import ethanjones.modularworld.block.factory.basic.BlockFactoryBedrock;
import ethanjones.modularworld.block.factory.basic.BlockFactoryDirt;
import ethanjones.modularworld.block.factory.basic.BlockFactoryGrass;
import ethanjones.modularworld.block.factory.basic.BlockFactoryStone;
import ethanjones.modularworld.core.util.Register;
import ethanjones.modularworld.side.common.ModularWorld;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class BlockFactories {
  @Register
  public static BlockFactoryBedrock bedrock;
  @Register
  public static BlockFactoryStone stone;
  @Register
  public static BlockFactoryDirt dirt;
  @Register
  public static BlockFactoryGrass grass;

  private static ArrayList<BlockFactory> factories = new ArrayList<BlockFactory>();

  public static void init() {
    for (Field f : BlockFactories.class.getFields()) {
      try {
        if (f.isAnnotationPresent(Register.class)) {
          Object o = f.getType().newInstance();
          if (o instanceof BlockFactory) {
            f.set(null, o);
            factories.add((BlockFactory) o);
            ModularWorld.blockManager.register((BlockFactory) o);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static void loadGraphics() {
    for (BlockFactory f : factories) {
      f.loadGraphics();
    }
  }
}
