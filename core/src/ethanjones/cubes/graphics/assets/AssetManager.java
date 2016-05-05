package ethanjones.cubes.graphics.assets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AssetManager {

  protected final HashMap<String, Asset> assets;
  private final String name;

  protected AssetManager(String name) {
    this.name = name.toLowerCase().replace(" ", "");
    this.assets = new HashMap<String, Asset>();
  }

  public String getName() {
    return name;
  }

  public Asset getAsset(String path) {
    return assets.get(path);
  }

  public ArrayList<Asset> getAll() {
    return getAssets("");
  }

  public ArrayList<Asset> getAssets(String prefix) {
    ArrayList<Asset> list = new ArrayList<Asset>();
    for (Map.Entry<String, Asset> entry : assets.entrySet()) {
      if (entry.getKey().startsWith(prefix)) {
        list.add(entry.getValue());
      }
    }
    return list;
  }
}
