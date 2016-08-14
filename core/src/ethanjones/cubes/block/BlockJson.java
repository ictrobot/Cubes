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

import java.util.Arrays;

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

    block.textures = parseMetaElement(json, "texture", new String[meta][], textureParser);
    block.lightLevel = parseMetaElement(json, "lightLevel", new Integer[meta], integerParser);
    block.transparent = parseMetaElement(json, "transparent", new Boolean[meta], booleanParser);

    JsonValue prop;

    prop = json.get("displayMeta");
    if (prop != null) {
      JsonArray array = prop.asArray();
      int[] ints = new int[array.size()];
      for (int i = 0; i < array.size(); i++) {
        ints[i] = array.get(i).asInt();
      }
      block.displayMeta = ints;
    } else {
      int[] ints = new int[meta];
      for (int i = 0; i < ints.length; i++) {
        ints[i] = i;
      }
      block.displayMeta = ints;
    }

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
        case "displayMeta":
        case "mining":
          break;
        default:
          throw new JsonException("Unexpected block member \"" + member.getName() + "\"");
      }
    }

    block.jsonFinish();
    IDManager.register(block);
  }

  private static <T> T[] parseMetaElement(JsonObject json, String name, T[] t, MetaElementParser<T> parser) {
    JsonValue j = json.get(name);
    if (j == null) return null;
    if (!j.isObject() || t.length == 1) {
      Arrays.fill(t, parser.parse(j));
    } else {
      JsonObject jsonObject = j.asObject();
      T defaultT = null;
      for (Member member : jsonObject) {
        String s = member.getName();
        if (s.equals("default")) {
          defaultT = parser.parse(member.getValue());
          continue;
        }
        int m = -1;
        try {
          m = Integer.parseInt(s);
          t[m] = null; // catch out of range exceptions
        } catch (Exception e) {
          throw new JsonException("Unexpected " + name + " member \"" + member.getName() + "\"");
        }
        t[m] = parser.parse(member.getValue());
      }
      for (int i = 0; i < t.length; i++) {
        if (t[i] == null) {
          if (defaultT == null) {
            throw new JsonException(name + " for meta " + i + " not defined");
          } else {
            t[i] = defaultT;
          }
        }
      }
    }
    return t;
  }

  public static boolean isJsonBlock(Block b) {
    return b instanceof JBlock;
  }

  private static class JBlock extends Block {
    protected Integer[] lightLevel;
    protected Boolean[] transparent;
    protected String[][] textures;
    protected int[] displayMeta;
    private final int meta;
    private boolean canBeTransparent;

    public JBlock(String id, int meta) {
      super(id);
      this.meta = meta;
    }

    private void jsonFinish() {
      if (transparent != null) {
        for (Boolean b : transparent) {
          if (b) {
            canBeTransparent = true;
            break;
          }
        }
      }
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
      return lightLevel == null ? 0 : lightLevel[meta];
    }

    @Override
    public boolean canBeTransparent() {
      return canBeTransparent;
    }

    @Override
    public boolean isTransparent(int meta) {
      return transparent == null ? false : transparent[meta];
    }

    @Override
    public int[] displayMetaValues() {
      return displayMeta;
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

  public static interface MetaElementParser<E> {
    public E parse(JsonValue prop);
  }

  private static final MetaElementParser<Integer> integerParser = new MetaElementParser<Integer>() {
    @Override
    public Integer parse(JsonValue prop) {
      return prop.asInt();
    }
  };

  private static final MetaElementParser<Boolean> booleanParser = new MetaElementParser<Boolean>() {
    @Override
    public Boolean parse(JsonValue prop) {
      return prop.asBoolean();
    }
  };

  private static final MetaElementParser<String[]> textureParser = new MetaElementParser<String[]>() {
    @Override
    public String[] parse(JsonValue prop) {
      String[] textures = new String[6];
      if (prop.isString()) {
        Arrays.fill(textures, prop.asString());
      } else {
        JsonObject texture = prop.asObject();
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
      }
      return textures;
    }
  };

}
