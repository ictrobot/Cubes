package ethanjones.cubes.core.json;

import com.badlogic.gdx.files.FileHandle;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonValue;
import ethanjones.cubes.block.Blocks;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.util.Multimap;
import ethanjones.cubes.graphics.assets.Asset;
import ethanjones.cubes.graphics.assets.AssetManager;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.item.Items;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class JsonLoader {

  private static Multimap<JsonStage, JsonValue> coreMap = new Multimap<JsonStage, JsonValue>();

  public static void loadCore() {
    AssetManager core = Assets.getCoreAssetManager();
    HashMap<String, FileHandle> map = new HashMap<String, FileHandle>();
    for (Asset j : core.getAssets("json/")) {
      map.put(j.getPath().substring(5), j.getFileHandle());
    }
    try {
      coreMap = load(map);
    } catch (IOException e) {
      throw new CubesException("Failed to load core json", e);
    }
  }

  private static Multimap<JsonStage, JsonValue> load(Map<String, FileHandle> map) throws IOException {
    Multimap<JsonStage, JsonValue> m = new Multimap<JsonStage, JsonValue>();
    for (Map.Entry<String, FileHandle> entry : map.entrySet()) {
      JsonStage stage = null;
      if (entry.getKey().startsWith("block")) {
        stage = JsonStage.BLOCK;
      } else if (entry.getKey().startsWith("item")) {
        stage = JsonStage.ITEM;
      } else if (entry.getKey().startsWith("recipe")) {
        stage = JsonStage.RECIPE;
      } else {
        throw new CubesException("Invalid json file path \"" + entry.getKey() + "\"");
      }

      Reader reader = entry.getValue().reader();
      try {
        m.put(stage, Json.parse(reader));
      } finally {
        reader.close();
      }
    }
    return m;
  }
  
  public static void firstStage() {
    firstStage(coreMap);
  
    Blocks.getInstances();
    Items.getInstances();
  }

  public static void firstStage(Multimap<JsonStage, JsonValue> map) {
    doStage(map, JsonStage.BLOCK);
    doStage(map, JsonStage.ITEM);
  }
  
  public static void secondStage() {
    secondStage(coreMap);
  }

  public static void secondStage(Multimap<JsonStage, JsonValue> map) {
    doStage(map, JsonStage.RECIPE);
  }

  private static void doStage(Multimap<JsonStage, JsonValue> map, JsonStage stage) {
    for (JsonValue value : map.remove(stage)) {
      stage.load(value);
    }
  }
}
