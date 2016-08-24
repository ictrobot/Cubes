package ethanjones.cubes.item.crafting;

import ethanjones.cubes.core.IDManager;
import ethanjones.cubes.core.json.JsonException;
import ethanjones.cubes.item.Item;
import ethanjones.cubes.item.ItemStack;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class RecipeJson {

  public static void json(JsonArray json) {
    for (JsonValue value : json) {
      JsonObject object = value.asObject();
      ItemStack output = parseStack(object.get("output"));
      JsonArray recipe = object.get("recipe").asArray();
      if (!(recipe.size() == 9 || recipe.size() == 4 || recipe.size() == 1))
        throw new JsonException("Invalid size " + recipe.size());
      CraftingInput[] inputs = new CraftingInput[recipe.size()];
      for (int i = 0; i < recipe.size(); i++) {
        JsonValue j = recipe.get(i);
        inputs[i] = parseInput(j);
      }
      CraftingManager.addRecipe(new CraftingRecipe(output, (Object[]) inputs));
    }
  }

  public static CraftingInput parseInput(JsonValue json) {
    ItemStack itemStack = parseStack(json);
    if (itemStack == null) return null;
    return new ItemStackInput(itemStack);
  }

  public static ItemStack parseStack(JsonValue json) {
    if (json.isString()) {
      return new ItemStack(getItem(json.asString()));
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

  public static Item getItem(String s) {
    Item item = IDManager.toItem(s);
    if (item == null) throw new JsonException("No such item \"" + s + "\"");
    return item;
  }
}
