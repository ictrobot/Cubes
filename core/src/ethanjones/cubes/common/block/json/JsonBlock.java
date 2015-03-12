package ethanjones.cubes.common.block.json;

import ethanjones.cubes.common.block.Block;
import ethanjones.cubes.common.core.mod.json.JsonBlockParameter;
import ethanjones.cubes.common.core.system.CubesException;
import ethanjones.cubes.common.core.util.BlockFace;
import ethanjones.cubes.client.graphics.assets.Assets;
import ethanjones.cubes.client.graphics.world.BlockTextureHandler;

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
