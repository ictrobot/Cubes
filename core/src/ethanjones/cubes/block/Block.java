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
    this.id = id;
    this.itemBlock = new ItemBlock(this);
  }

  public void loadGraphics() {
    TextureRegion textureRegion = Assets.getPackedTexture(id + ".png");
    if (textureRegion == null) {
      int index = id.indexOf(":");
      textureRegion = Assets.getPackedTexture(id.substring(0, index) + ":block/" + id.substring(index + 1) + ".png");
      if (textureRegion == null) {
        throw new CubesException("Can't find block texture for " + id);
      }
    }
    textureHandler = new BlockTextureHandler(textureRegion);
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

  @Override
  public String toString() {
    return id;
  }
}
