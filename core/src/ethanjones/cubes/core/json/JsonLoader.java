package ethanjones.cubes.core.json;

import ethanjones.cubes.block.Blocks;
import ethanjones.cubes.core.IDManager;
import ethanjones.cubes.core.mod.json.JsonModInstance;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.util.Multimap;
import ethanjones.cubes.graphics.assets.Asset;
import ethanjones.cubes.graphics.assets.AssetManager;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.item.Items;

import com.badlogic.gdx.files.FileHandle;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonValue;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class JsonLoader {

  private static Multimap<JsonStage, JsonValue> multimap = new Multimap<JsonStage, JsonValue>();

  public static void loadCore() {
    AssetManager core = Assets.getCoreAssetManager();
    HashMap<String, FileHandle> map = new HashMap<String, FileHandle>();
    for (Asset j : core.getAssets("json/")) {
      map.put(j.getPath().substring(5), j.getFileHandle());
    }
    try {
      load(map);
    } catch (IOException e) {
      throw new CubesException("Failed to load core json", e);
    }
  }

  public static void load(JsonModInstance mod) throws IOException {
    load(mod.jsonFiles);
  }

  private static void load(Map<String, FileHandle> map) throws IOException {
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
        multimap.put(stage, Json.parse(reader));
      } finally {
        reader.close();
      }
    }
  }

  public static void firstStage() {
    doStage(JsonStage.BLOCK);
    doStage(JsonStage.ITEM);

    IDManager.getInstances(Blocks.class);
    IDManager.getInstances(Items.class);
  }

  public static void secondStage() {
    doStage(JsonStage.RECIPE);
  }

  private static void doStage(JsonStage stage) {
    for (JsonValue value : multimap.remove(stage)) {
      stage.load(value);
    }
  }
}
