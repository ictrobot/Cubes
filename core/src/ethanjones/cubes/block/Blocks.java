package ethanjones.cubes.block;

import java.lang.reflect.Field;
import java.util.ArrayList;

import ethanjones.cubes.block.basic.BlockBedrock;
import ethanjones.cubes.block.basic.BlockDirt;
import ethanjones.cubes.block.basic.BlockGrass;
import ethanjones.cubes.block.basic.BlockStone;
import ethanjones.cubes.core.util.Register;

public class Blocks {

  @Register
  public static BlockBedrock bedrock;
  @Register
  public static BlockStone stone;
  @Register
  public static BlockDirt dirt;
  @Register
  public static BlockGrass grass;

  private static ArrayList<Block> blocks = new ArrayList<Block>();

  public static void init() {
    for (Field f : Blocks.class.getFields()) {
      try {
        if (f.isAnnotationPresent(Register.class)) {
          if (f.get(null) != null) continue;
          Object o = f.getType().newInstance();
          if (o instanceof Block) {
            Block block = (Block) o;
            f.set(null, block);
            blocks.add(block);
            BlockManager.register(block);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static void loadGraphics() {
    for (Block f : blocks) {
      f.loadGraphics();
    }
  }
}
