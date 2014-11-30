package ethanjones.cubes.block;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ethanjones.cubes.block.basic.BlockBedrock;
import ethanjones.cubes.block.basic.BlockDirt;
import ethanjones.cubes.block.basic.BlockGrass;
import ethanjones.cubes.block.basic.BlockStone;
import ethanjones.cubes.core.util.Register;
import ethanjones.cubes.side.Sided;

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
  private static List<Block> blocksUnmodifiable = Collections.unmodifiableList(blocks);

  public static void init() {
    for (Field f : Blocks.class.getFields()) {
      try {
        if (f.isAnnotationPresent(Register.class)) {
          if (f.get(null) != null) continue;
          Object o = f.getType().newInstance();
          if (o instanceof Block) {
            f.set(null, o);
            blocks.add((Block) o);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static void register() {
    for (Field f : Blocks.class.getFields()) {
      try {
        if (f.isAnnotationPresent(Register.class)) {
          Object o = f.get(null);
          if (o instanceof Block) {
            Sided.getBlockManager().register((Block) o);
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

  public static List<Block> getBlocks() {
    return blocksUnmodifiable;
  }
}
