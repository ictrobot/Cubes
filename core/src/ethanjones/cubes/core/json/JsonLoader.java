package ethanjones.cubes.core.json;

import ethanjones.cubes.block.Blocks;
import ethanjones.cubes.block.BlockJson;
import ethanjones.cubes.core.IDManager;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.mod.json.JsonModInstance;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.graphics.assets.Asset;
import ethanjones.cubes.graphics.assets.AssetManager;
import ethanjones.cubes.graphics.assets.Assets;

import com.badlogic.gdx.files.FileHandle;
import com.eclipsesource.json.Json;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

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
    IDManager.getBlocks(Blocks.class);
  }

  public static void load(JsonModInstance mod) throws IOException {
    load(mod.jsonFiles);
  }

  private static void load(Map<String, FileHandle> map) throws IOException {
    for (Map.Entry<String, FileHandle> entry : map.entrySet()) {
      if (entry.getKey().startsWith("block")) {
        Reader reader = entry.getValue().reader();
        try {
          BlockJson.json(Json.parse(reader).asArray());
        } finally {
          reader.close();
        }
      } else {
        throw new CubesException("Invalid json file path \"" + entry.getKey() + "\"");
      }
    }
  }
}