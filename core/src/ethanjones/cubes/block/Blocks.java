package ethanjones.cubes.block;

import ethanjones.cubes.core.id.IDManager;

public class Blocks {

  public static Block bedrock;
  public static Block stone;
  public static Block dirt;
  public static Block grass;
  public static Block log;
  public static Block leaves;
  public static Block glow;
  public static Block glass;
  public static Block chest;
  public static Block sapling;

  public static void init() {
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

  public static void getInstances() {
    bedrock = IDManager.toBlock("core:bedrock");
    stone = IDManager.toBlock("core:stone");
    dirt = IDManager.toBlock("core:dirt");
    log = IDManager.toBlock("core:log");
    glow = IDManager.toBlock("core:glow");
  }
}
