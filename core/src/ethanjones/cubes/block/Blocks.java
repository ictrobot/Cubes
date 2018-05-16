package ethanjones.cubes.block;

import ethanjones.cubes.block.blocks.*;
import ethanjones.cubes.core.id.GetInstances.GetBlock;
import ethanjones.cubes.core.id.IDManager;

public class Blocks {

  @GetBlock("core:bedrock")
  public static Block bedrock;
  @GetBlock("core:stone")
  public static Block stone;

  public static Block dirt;

  public static Block grass;
  @GetBlock("core:log")
  public static Block log;
  
  public static Block leaves;
  @GetBlock("core:glow")
  public static Block glow;
  
  public static Block glass;

  public static Block chest;
  
  public static Block sapling;

  public static void init() {
    dirt = new BlockDirt();
    IDManager.register(dirt);
    grass = new BlockGrass();
    IDManager.register(grass);
    leaves = new BlockLeaves();
    IDManager.register(leaves);
    glass = new BlockGlass();
    IDManager.register(glass);
    chest = new BlockChest();
    IDManager.register(chest);
    sapling = new BlockSapling();
    IDManager.register(sapling);
  }
}
