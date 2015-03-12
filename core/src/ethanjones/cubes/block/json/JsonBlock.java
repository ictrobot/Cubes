package ethanjones.cubes.block.json;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.mod.json.JsonBlockParameter;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.util.BlockFace;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.world.BlockTextureHandler;

public class JsonBlock extends Block {
  
  private final JsonBlockParameter jsonBlockParameter;

  public JsonBlock(JsonBlockParameter jsonBlockParameter) {
    super(jsonBlockParameter.fullID);
    this.jsonBlockParameter = jsonBlockParameter;
  }

  @Override
  public void loadGraphics() {
    try {
      if (jsonBlockParameter.singleTexture != null) {
        textureHandler = new BlockTextureHandler(Assets.getBlockTexture(jsonBlockParameter.singleTexture));
      } else if (jsonBlockParameter.textures != null) {
        textureHandler = new BlockTextureHandler(Assets.getBlockTexture(jsonBlockParameter.textures[0]));
      } else {
        super.loadGraphics();
      }
      if (jsonBlockParameter.textures != null) {
        for (int i = 0; i < BlockFace.values().length; i++) {
          if (jsonBlockParameter.textures[i] == null) continue;
          textureHandler.setSide(BlockFace.values()[i], jsonBlockParameter.textures[i]);
        }
      }
    } catch (NullPointerException e) {
      throw new CubesException("Failed to load texture for " + id, e);
    }
  }
}
