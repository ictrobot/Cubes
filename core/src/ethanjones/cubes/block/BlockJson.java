package ethanjones.cubes.block;

import ethanjones.cubes.core.IDManager;
import ethanjones.cubes.core.json.JsonException;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.util.BlockFace;
import ethanjones.cubes.graphics.world.BlockTextureHandler;
import ethanjones.cubes.item.ItemJson;
import ethanjones.cubes.item.ItemTool;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class BlockJson {

  public static void json(JsonArray json) {
    for (JsonValue value : json) {
      addBlock(value.asObject());
    }
  }

  public static void addBlock(JsonObject json) {
    String id = json.getString("id", null);
    if (id == null) throw new JsonException("No block id");
    JBlock block = new JBlock(id);

    JsonValue prop;

    prop = json.get("texture");
    if (prop != null) {
      if (prop.isString()) {
        block.textures = new String[]{prop.asString()};
      } else {
        JsonObject texture = prop.asObject();
        block.textures = new String[6];
        for (JsonObject.Member member : texture) {
          switch (member.getName()) {
            case "posX":
              block.textures[BlockFace.posX.index] = member.getValue().asString();
              break;
            case "negX":
              block.textures[BlockFace.negX.index] = member.getValue().asString();
              break;
            case "posY":
            case "top":
              block.textures[BlockFace.posY.index] = member.getValue().asString();
              break;
            case "negY":
            case "bottom":
              block.textures[BlockFace.negY.index] = member.getValue().asString();
              break;
            case "posZ":
              block.textures[BlockFace.posZ.index] = member.getValue().asString();
              break;
            case "negZ":
              block.textures[BlockFace.negZ.index] = member.getValue().asString();
              break;
            case "side":
              block.textures[BlockFace.posX.index] = member.getValue().asString();
              block.textures[BlockFace.negX.index] = member.getValue().asString();
              block.textures[BlockFace.posZ.index] = member.getValue().asString();
              block.textures[BlockFace.negZ.index] = member.getValue().asString();
              break;
            default:
              throw new JsonException("Unexpected block texture member \"" + member.getName() + "\"");
          }
        }
      }
    } else {
      block.textures = new String[]{id};
    }

    prop = json.get("lightLevel");
    if (prop != null) block.lightLevel = prop.asInt();

    prop = json.get("transparent");
    if (prop != null) block.transparent = prop.asBoolean();

    prop = json.get("mining");
    if (prop != null) {
      JsonObject object = prop.asObject();
      for (JsonObject.Member member : object) {
        switch (member.getName()) {
          case "speed":
            block.setMiningTime(member.getValue().asFloat());
            break;
          case "tool":
            block.setMiningTool(ItemJson.toolType(member.getValue().asString()));
            break;
          case "toolLevel":
            block.setMiningToolLevel(member.getValue().asInt());
            break;
          case "other":
            block.setMiningOther(member.getValue().asBoolean());
            break;
          default:
            throw new JsonException("Unexpected item tool member \"" + member.getName() + "\"");
        }
      }
    }

    for (JsonObject.Member member : json) {
      switch (member.getName()) {
        case "id":
        case "texture":
        case "lightLevel":
        case "transparent":
        case "mining":
          break;
        default:
          throw new JsonException("Unexpected block member \"" + member.getName() + "\"");
      }
    }

    IDManager.register(block);
  }

  public static boolean isJsonBlock(Block b) {
    return b instanceof JBlock;
  }

  private static class JBlock extends Block {
    protected int lightLevel = 0;
    protected boolean transparent = false;
    protected String[] textures;

    public JBlock(String id) {
      super(id);
    }

    @Override
    public void loadGraphics() {
      textureHandler = new BlockTextureHandler(textures[0]);
      if (textures.length == 6) {
        for (int i = 1; i < textures.length; i++) {
          textureHandler.setSide(i, textures[i]);
        }
      } else if (textures.length != 1) {
        throw new CubesException("Invalid JBlock.textures length for id \"" + id + "\"");
      }
    }

    @Override
    public int getLightLevel() {
      return lightLevel;
    }

    @Override
    public boolean isTransparent() {
      return transparent;
    }

    protected void setMiningTime(float miningTime) {
      this.miningTime = miningTime;
    }

    protected void setMiningTool(ItemTool.ToolType miningTool) {
      this.miningTool = miningTool;
    }

    protected void setMiningToolLevel(int miningToolLevel) {
      this.miningToolLevel = miningToolLevel;
    }

    protected void setMiningOther(boolean miningOther) {
      this.miningOther = miningOther;
    }
  }
}
