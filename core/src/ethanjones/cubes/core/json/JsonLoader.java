package ethanjones.cubes.core.json;

import ethanjones.cubes.block.BlockJson;
import ethanjones.cubes.block.Blocks;
import ethanjones.cubes.core.IDManager;
import ethanjones.cubes.core.mod.json.JsonModInstance;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.graphics.assets.Asset;
import ethanjones.cubes.graphics.assets.AssetManager;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.item.ItemJson;
import ethanjones.cubes.item.Items;
import ethanjones.cubes.item.crafting.RecipeJson;

import com.badlogic.gdx.files.FileHandle;
import com.eclipsesource.json.Json;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class JsonLoader {

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
    IDManager.getInstances(Blocks.class);
    IDManager.getInstances(Items.class);
  }

  public static void load(JsonModInstance mod) throws IOException {
    load(mod.jsonFiles);
  }

  private static void load(Map<String, FileHandle> map) throws IOException {
    List<Entry<String, FileHandle>> delayed = new ArrayList<Entry<String, FileHandle>>();
    for (Map.Entry<String, FileHandle> entry : map.entrySet()) {
      if (entry.getKey().startsWith("block")) {
        Reader reader = entry.getValue().reader();
        try {
          BlockJson.json(Json.parse(reader).asArray());
        } finally {
          reader.close();
        }
      } else if (entry.getKey().startsWith("item")) {
        Reader reader = entry.getValue().reader();
        try {
          ItemJson.json(Json.parse(reader).asArray());
        } finally {
          reader.close();
        }
      } else if (entry.getKey().startsWith("recipe")) {
        delayed.add(entry); // needs to be done after item & block loading
      } else {
        throw new CubesException("Invalid json file path \"" + entry.getKey() + "\"");
      }
    }
    for (Entry<String, FileHandle> entry : delayed) {
      if (entry.getKey().startsWith("recipe")) {
        Reader reader = entry.getValue().reader();
        try {
          RecipeJson.json(Json.parse(reader).asObject());
        } finally {
          reader.close();
        }
      }
    }
  }
}
