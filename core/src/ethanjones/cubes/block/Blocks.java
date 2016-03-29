package ethanjones.cubes.block;

import ethanjones.cubes.block.basic.BlockGrass;
import ethanjones.cubes.core.IDManager;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.util.Register;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Blocks {

  @Register("core:bedrock")
  public static Block bedrock;
  @Register("core:stone")
  public static Block stone;
  @Register("core:dirt")
  public static Block dirt;
  @Register
  public static BlockGrass grass;
  @Register("core:wood")
  public static Block wood;
  @Register("core:leaves")
  public static Block leaves;

  public static void init() {
    for (Field f : Blocks.class.getFields()) {
      try {
        if (f.isAnnotationPresent(Register.class)) {
          if (f.get(null) != null) continue;
          Class<?> type = f.getType();
          Block block;

          if (type == Block.class) {
            Register register = f.getAnnotation(Register.class);
            block = new Block(register.value());
          } else {
            Object o = type.newInstance();
            block = (Block) o;
          }

          f.set(null, block);
          IDManager.register(block);
        }
      } catch (Exception e) {
        Log.error("Failed to init block", e);
      }
    }
  }
}
