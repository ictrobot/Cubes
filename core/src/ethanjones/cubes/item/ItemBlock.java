package ethanjones.cubes.item;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.graphics.assets.Assets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ItemBlock extends Item {
  public ItemBlock(Block block) {
    super(block.id);
  }

  public void loadGraphics() {
    TextureRegion textureRegion = Assets.getPackedTexture(id + ".png");
    if (textureRegion == null) {
      int index = id.indexOf(":");
      textureRegion = Assets.getPackedTexture(id.substring(0, index) + ":block/" + id.substring(index + 1) + ".png");
      if (textureRegion == null) {
        throw new CubesException("Can't find item texture for " + id);
      }
    }
    this.texture = textureRegion;
  }
}
