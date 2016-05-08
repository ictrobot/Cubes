package ethanjones.cubes.item;

import ethanjones.cubes.core.IDManager;
import ethanjones.cubes.core.json.JsonException;
import ethanjones.cubes.graphics.assets.Assets;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class ItemJson {

  public static void json(JsonArray json) {
    for (JsonValue value : json) {
      addItem(value.asObject());
    }
  }

  public static void addItem(JsonObject json) {
    if (json.get("tool") != null) {
      addItemTool(json);
      return;
    }
    String id = json.getString("id", null);
    if (id == null) throw new JsonException("No item id");
    JItem item = new JItem(id);

    JsonValue prop;

    prop = json.get("texture");
    if (prop != null) {
      item.texture = prop.asString();
    } else {
      item.texture = id;
    }

    for (JsonObject.Member member : json) {
      switch (member.getName()) {
        case "id":
        case "texture":
          break;
        default:
          throw new JsonException("Unexpected block member \"" + member.getName() + "\"");
      }
    }

    IDManager.register(item);
  }

  public static void addItemTool(JsonObject json) {
    String id = json.getString("id", null);
    if (id == null) throw new JsonException("No item id");
    JItemTool item = new JItemTool(id);

    JsonObject tool = json.get("tool").asObject();
    for (JsonObject.Member member : tool) {
      switch (member.getName()) {
        case "type":
          item.setToolType(toolType(member.getValue().asString()));
          break;
        case "level":
          item.setToolLevel(member.getValue().asInt());
          break;
        default:
          throw new JsonException("Unexpected item tool member \"" + member.getName() + "\"");
      }
    }

    JsonValue prop;

    prop = json.get("texture");
    if (prop != null) {
      item.texture = prop.asString();
    } else {
      item.texture = id;
    }

    for (JsonObject.Member member : json) {
      switch (member.getName()) {
        case "id":
        case "texture":
        case "tool":
          break;
        default:
          throw new JsonException("Unexpected block member \"" + member.getName() + "\"");
      }
    }

    IDManager.register(item);
  }

  public static ItemTool.ToolType toolType(String s) {
    switch (s) {
      case "pickaxe":
        return ItemTool.ToolType.pickaxe;
      case "axe":
        return ItemTool.ToolType.axe;
      case "shovel":
        return ItemTool.ToolType.shovel;
      default:
        throw new JsonException("No such tool: \"" + s + "\"");
    }
  }

  public static boolean isJsonItem(Item i) {
    return i instanceof JItem || i instanceof JItemTool;
  }

  private static class JItem extends Item {
    protected String texture;

    public JItem(String id) {
      super(id);
    }

    @Override
    public void loadGraphics() {
      ((Item) this).texture = Assets.getPackedTextureFromID(texture, "item");
    }
  }

  private static class JItemTool extends ItemTool {
    protected String texture;

    public JItemTool(String id) {
      super(id);
    }

    @Override
    public void loadGraphics() {
      ((Item) this).texture = Assets.getPackedTextureFromID(texture, "item");
    }

    protected void setToolType(ToolType type) {
      this.toolType = type;
    }

    protected void setToolLevel(int level) {
      this.toolLevel = level;
    }
  }
}
