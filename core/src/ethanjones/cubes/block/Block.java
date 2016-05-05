package ethanjones.cubes.block;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.world.BlockTextureHandler;
import ethanjones.cubes.item.ItemBlock;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Block {

  public String id;
  protected ItemBlock itemBlock;
  protected BlockTextureHandler textureHandler;

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
}
