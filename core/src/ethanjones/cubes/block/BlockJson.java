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
import com.eclipsesource.json.JsonObject.Member;
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
    int meta = json.getInt("meta", 1);
    JBlock block = new JBlock(id, meta);

    JsonValue prop;

    prop = json.get("texture");
    if (prop != null) {
      if (meta == 1) {
        block.textures = new String[1][];
        block.textures[0] = parseTextures(prop);
      } else {
        block.textures = new String[meta][];
        JsonObject jsonObject = prop.asObject();
        String[] defaultTextures = null;
        for (Member member : jsonObject) {
          String s = member.getName();
          if (s.equals("default")) {
            defaultTextures = parseTextures(member.getValue());
            continue;
          }
          int m = -1;
          try {
            m = Integer.parseInt(s);
          } catch (NumberFormatException e) {
            throw new JsonException("Unexpected texture member \"" + member.getName() + "\"");
          }
          block.textures[m] = parseTextures(member.getValue());
        }
        for (int i = 0; i < block.textures.length; i++) {
          if (block.textures[i] == null) {
            if (defaultTextures == null) {
              throw new JsonException("Textures for meta " + i + " not defined");
            } else {
              block.textures[i] = defaultTextures;
            }
          }
        }
      }
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
        case "meta":
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

  private static String[] parseTextures(JsonValue prop) {
    if (prop.isString()) {
      return new String[]{prop.asString()};
    } else {
      JsonObject texture = prop.asObject();
      String[] textures = new String[6];
      for (JsonObject.Member member : texture) {
        String value = member.getValue().asString();
        switch (member.getName()) {
          case "posX":
            textures[BlockFace.posX.index] = value;
            break;
          case "negX":
            textures[BlockFace.negX.index] = value;
            break;
          case "posY":
          case "top":
            textures[BlockFace.posY.index] = value;
            break;
          case "negY":
          case "bottom":
            textures[BlockFace.negY.index] = value;
            break;
          case "posZ":
            textures[BlockFace.posZ.index] = value;
            break;
          case "negZ":
            textures[BlockFace.negZ.index] = value;
            break;
          case "side":
            textures[BlockFace.posX.index] = value;
            textures[BlockFace.negX.index] = value;
            textures[BlockFace.posZ.index] = value;
            textures[BlockFace.negZ.index] = value;
            break;
          case "other":
            for (int i = 0; i < textures.length; i++) {
              if (textures[i] == null) textures[i] = value;
            }
            break;
          default:
            throw new JsonException("Unexpected block texture member \"" + member.getName() + "\"");
        }
      }
      return textures;
    }
  }

  public static boolean isJsonBlock(Block b) {
    return b instanceof JBlock;
  }

  private static class JBlock extends Block {
    protected int lightLevel = 0;
    protected boolean transparent = false;
    protected String[][] textures;
    private final int meta;

    public JBlock(String id, int meta) {
      super(id);
      this.meta = meta;
    }

    @Override
    public void loadGraphics() {
      if (textures == null) {
        super.loadGraphics();
        return;
      }
      textureHandlers = new BlockTextureHandler[meta];
      for (int m = 0; m < textureHandlers.length; m++) {
        textureHandlers[m] = new BlockTextureHandler(textures[m][0]);
        if (textures[m].length == 6) {
          for (int s = 1; s < textures[m].length; s++) {
            textureHandlers[m].setSide(s, textures[m][s]);
          }
        } else {
          throw new CubesException("Invalid JBlock.textures length for id \"" + id + "\"");
        }
      }
      textures = null;
    }

    @Override
    public int getLightLevel(int meta) {
      return lightLevel;
    }

    @Override
    public boolean isTransparent(int meta) {
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
