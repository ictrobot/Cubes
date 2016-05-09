package ethanjones.cubes.block;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.graphics.world.BlockTextureHandler;
import ethanjones.cubes.item.ItemBlock;
import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.ItemTool;

public class Block {

  public String id;
  protected ItemBlock itemBlock;
  protected BlockTextureHandler textureHandler;
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
    textureHandler = new BlockTextureHandler(id);
  }

  public BlockTextureHandler getTextureHandler() {
    return textureHandler;
  }

  public ItemBlock getItemBlock() {
    return itemBlock;
  }

  public String getName() {
    return Localization.get("block." + id.replaceFirst(":", "."));
  }

  public int getLightLevel() {
    return 0;
  }

  public boolean isTransparent() {
    return false;
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
}
