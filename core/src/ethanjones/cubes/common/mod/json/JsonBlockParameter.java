package ethanjones.cubes.common.mod.json;

import ethanjones.cubes.common.block.BlockManager;
import ethanjones.cubes.common.block.json.JsonBlock;

public class JsonBlockParameter {

  public String id;
  public String singleTexture;
  public String[] textures;

  public String fullID;
  private JsonBlock jsonBlock;

  public void init(JsonModInstance jsonModInstance) {
    jsonBlock = new JsonBlock(this);
  }

  public void loadGraphics() {
    jsonBlock.loadGraphics();
  }

  public void register(JsonModInstance jsonModInstance) {
    BlockManager.register(jsonBlock);
  }
}
