package ethanjones.cubes.block;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.graphics.world.BlockTextureHandler;
import ethanjones.cubes.item.ItemBlock;
import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.ItemTool;

public class Block {
  private static final int[] ONE_ZERO = new int[]{0};

  public String id;
  protected ItemBlock itemBlock;
  protected BlockTextureHandler[] textureHandlers;
  // block mining
  protected float miningTime = 0.5f;
  protected ItemTool.ToolType miningTool = ItemTool.ToolType.pickaxe;
  protected int miningToolLevel = 1;
  protected boolean miningOther = true;

  public Block(String id) {
    if (!id.contains(":")) throw new IllegalArgumentException(id + " is not in the correct format");
    this.id = id.toLowerCase();
    this.itemBlock = new ItemBlock(this);
  }

  public void loadGraphics() {
    textureHandlers = new BlockTextureHandler[]{new BlockTextureHandler(id)};
  }

  public BlockTextureHandler getTextureHandler(int meta) {
    if (meta < 0 || meta >= textureHandlers.length) meta = 0;
    return textureHandlers[meta];
  }

  public ItemBlock getItemBlock() {
    return itemBlock;
  }

  public String getName() {
    return Localization.get("block." + id.replaceFirst(":", "."));
  }

  public int getLightLevel(int meta) {
    return 0;
  }

  public boolean canBeTransparent() {
    return false; // should be true if it is possible for isTransparent to return true
  }

  public boolean isTransparent(int meta) {
    return false;
  }

  public int[] displayMetaValues() {
    return ONE_ZERO;
  }

  @Override
  public String toString() {
    return id;
  }

  // block mining
  public boolean canMine(ItemStack itemStack) {
    if (itemStack == null || !(itemStack.item instanceof ItemTool)) return miningOther;
    ItemTool itemTool = ((ItemTool) itemStack.item);
    if (itemTool.getToolType() != miningTool) return miningOther;
    return miningOther || miningToolLevel >= itemTool.getToolLevel();
  }

  public float getMiningTime() {
    return miningTime;
  }

  public float getMiningSpeed(ItemStack itemStack) {
    if (itemStack == null || !(itemStack.item instanceof ItemTool)) return 1f;
    ItemTool itemTool = ((ItemTool) itemStack.item);
    if (itemTool.getToolType() != miningTool) return 1f;
    return itemTool.getToolLevel() * 2;
  }

  public boolean onButtonPress(int button, Player player, int blockX, int blockY, int blockZ) {
    return false;
  }
}
