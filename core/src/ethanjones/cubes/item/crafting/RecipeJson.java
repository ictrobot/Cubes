package ethanjones.cubes.item.crafting;

import ethanjones.cubes.core.IDManager;
import ethanjones.cubes.core.json.JsonException;
import ethanjones.cubes.item.Item;
import ethanjones.cubes.item.ItemStack;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonObject.Member;
import com.eclipsesource.json.JsonValue;

import java.util.HashMap;

public class RecipeJson {

  public static void json(JsonObject json) {
    HashMap<String, ItemStack> mappings = new HashMap<String, ItemStack>();
    for (Member member : json) {
      JsonValue value = member.getValue();
      if (value.isArray()) continue;
      String mappingName = member.getName();
      ItemStack mappingValue = parseStack(value, null);
      mappings.put(mappingName, mappingValue);
    }
    for (Member member : json) {
      JsonValue value = member.getValue();
      if (!value.isArray()) continue;
      JsonArray array = value.asArray();
      if (!(array.size() == 9 || array.size() == 4 || array.size() == 1))
        throw new JsonException("Invalid size " + array.size());
      ItemStack output = parseStack(Json.value(member.getName()), mappings);
      CraftingInput[] inputs = new CraftingInput[array.size()];
      for (int i = 0; i < array.size(); i++) {
        JsonValue j = array.get(i);
        inputs[i] = parseInput(j, mappings);
      }
      CraftingManager.addRecipe(new CraftingRecipe(output, (Object[]) inputs));
    }
  }

  public static CraftingInput parseInput(JsonValue json, HashMap<String, ItemStack> mappings) {
    ItemStack itemStack = parseStack(json, mappings);
    if (itemStack == null) return null;
    return new ItemStackInput(itemStack);
  }

  public static ItemStack parseStack(JsonValue json, HashMap<String, ItemStack> mappings) {
    if (json.isString()) {
      return parseStackString(json.asString(), mappings);
    } else if (json.isObject()) {
      JsonObject obj = json.asObject();
      JsonValue id = obj.get("id");
      if (id == null) throw new JsonException("No id");
      return new ItemStack(getItem(id.asString()), obj.getInt("count", 1), obj.getInt("meta", 0));
    } else if (json.isNull()) {
      return null;
    }
    throw new JsonException("Invalid type " + json.toString());
  }

  public static ItemStack parseStackString(String s, HashMap<String, ItemStack> mappings) {
    if (mappings != null && mappings.containsKey(s)) {
      return mappings.get(s);
    }
    return new ItemStack(getItem(s));
  }

  public static Item getItem(String s) {
    Item item = IDManager.toItem(s);
    if (item == null) throw new JsonException("No such item \"" + s + "\"");
    return item;
  }
}
