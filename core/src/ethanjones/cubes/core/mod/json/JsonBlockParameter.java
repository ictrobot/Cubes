package ethanjones.cubes.core.mod.json;

import ethanjones.cubes.block.json.JsonBlock;
import ethanjones.cubes.side.Sided;

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
    Sided.getBlockManager().register(jsonBlock);
  }
}
