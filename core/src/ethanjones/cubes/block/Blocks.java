package ethanjones.cubes.block;

import ethanjones.cubes.block.basic.BlockGrass;
import ethanjones.cubes.block.basic.BlockGlow;
import ethanjones.cubes.block.basic.BlockLog;
import ethanjones.cubes.block.basic.BlockTransparent;
import ethanjones.cubes.core.IDManager;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.util.Register;

import java.lang.reflect.Field;

public class Blocks {

  @Register("core:bedrock")
  public static Block bedrock;
  @Register("core:stone")
  public static Block stone;
  @Register("core:dirt")
  public static Block dirt;
  @Register
  public static BlockGrass grass;
  @Register
  public static BlockLog log;
  @Register("core:leaves")
  public static BlockTransparent leaves;
  @Register
  public static BlockGlow glow;
  @Register
  public static BlockTransparent glass;

  public static void init() {
    for (Field f : Blocks.class.getFields()) {
      try {
        if (f.isAnnotationPresent(Register.class)) {
          if (f.get(null) != null) continue;
          Register register = f.getAnnotation(Register.class);
          Class<?> type = f.getType();
          Block block;

          if (type == Block.class) {
            block = new Block(register.value());
          } else {
            if (register.value().isEmpty()) {
              Object o = type.newInstance();
              block = (Block) o;
            } else {
              Object o = type.getConstructor(String.class).newInstance(register.value());
              block = (Block) o;
            }
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
