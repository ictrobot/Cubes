package ethanjones.cubes.common.block;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ethanjones.cubes.common.core.localization.Localization;
import ethanjones.cubes.common.core.system.CubesException;
import ethanjones.cubes.client.graphics.assets.Assets;
import ethanjones.cubes.client.graphics.world.BlockTextureHandler;

public abstract class Block {

  protected String id;

  protected BlockTextureHandler textureHandler;

  public Block(String id) {
    if (!id.contains(":")) throw new IllegalArgumentException(id + " is not in the correct format");
    this.id = id;
  }

  public void loadGraphics() {
    TextureRegion textureRegion = Assets.getBlockTexture(id + ".png");
    if (textureRegion == null) {
      int index = id.indexOf(":");
      textureRegion = Assets.getBlockTexture(id.substring(0, index) + ":block/" + id.substring(index + 1) + ".png");
      if (textureRegion == null) {
        throw new CubesException("Can't find block texture for " + id);
      }
    }
    textureHandler = new BlockTextureHandler(textureRegion);
  }

  public BlockTextureHandler getTextureHandler() {
    return textureHandler;
  }

  public String getName() {
    return Localization.get("block." + id.replaceFirst(":", "."));
  }
}
