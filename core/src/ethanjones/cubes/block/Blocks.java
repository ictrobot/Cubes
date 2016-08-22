package ethanjones.cubes.block;

import ethanjones.cubes.core.IDManager;
import ethanjones.cubes.core.IDManager.GetBlock;

public class Blocks {

  @GetBlock("core:bedrock")
  public static Block bedrock;
  @GetBlock("core:stone")
  public static Block stone;
  @GetBlock("core:dirt")
  public static Block dirt;
  @GetBlock("core:grass")
  public static Block grass;
  @GetBlock("core:log")
  public static Block log;
  @GetBlock("core:leaves")
  public static Block leaves;
  @GetBlock("core:glow")
  public static Block glow;
  @GetBlock("core:glass")
  public static Block glass;

  public static Block craft;

  public static void init() {
    craft = new BlockCraft();
    IDManager.register(craft);
  }
}
