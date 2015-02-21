package ethanjones.cubes.common.block;

import java.lang.reflect.Field;
import java.util.ArrayList;

import ethanjones.cubes.common.block.basic.BlockBedrock;
import ethanjones.cubes.common.block.basic.BlockDirt;
import ethanjones.cubes.common.block.basic.BlockGrass;
import ethanjones.cubes.common.block.basic.BlockStone;
import ethanjones.cubes.common.core.logging.Log;
import ethanjones.cubes.common.core.util.Register;

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
        Log.error("Failed to init block", e);
      }
    }
  }

  public static void loadGraphics() {
    for (Block f : blocks) {
      f.loadGraphics();
    }
  }
}
