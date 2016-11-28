package ethanjones.cubes.block;

import ethanjones.cubes.core.id.GetInstances.GetBlock;
import ethanjones.cubes.core.id.IDManager;

public class Blocks {

  @GetBlock("core:bedrock")
  public static Block bedrock;
  @GetBlock("core:stone")
  public static Block stone;
  @GetBlock("core:dirt")
  public static Block dirt;

  public static Block grass;
  @GetBlock("core:log")
  public static Block log;
  @GetBlock("core:leaves")
  public static Block leaves;
  @GetBlock("core:glow")
  public static Block glow;
  @GetBlock("core:glass")
  public static Block glass;

  public static Block chest;

  public static void init() {
    grass = new BlockGrass();
    IDManager.register(grass);
    chest = new BlockChest();
    IDManager.register(chest);
  }
}
