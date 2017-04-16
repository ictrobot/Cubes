package ethanjones.cubes.graphics.assets;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.system.CubesException;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Assets {

  public static final String CORE = "core";
  public static PackedTextureSheet packedTextureSheet;
  protected static HashMap<String, AssetManager> assetManagers = new HashMap<String, AssetManager>();

  public static AssetManager getCoreAssetManager() {
    return getAssetManager(CORE);
  }

  public static AssetManager getAssetManager(String name) {
    return assetManagers.get(name);
  }

  public static List<AssetManager> getAssetManagers() {
    List<AssetManager> list = new ArrayList<AssetManager>();
    list.addAll(assetManagers.values());
    return list;
  }

  public static TextureRegion getTextureRegion(String assetName) {
    Asset asset = getAsset(assetName);
    if (asset == null) return null;
    return asset.getTextureRegion();
  }

  public static Texture getTexture(String assetName) {
    Asset asset = getAsset(assetName);
    if (asset == null) return null;
    return asset.getTexture();
  }
  
  public static Material getMaterial(String assetName) {
    Asset asset = getAsset(assetName);
    if (asset == null) return null;
    return asset.getMaterial();
  }

  public static Asset getAsset(String name) {
    int index = name.indexOf(":");
    if (index == -1) return null;
    String assetManagerName = name.substring(0, index);
    AssetManager assetManager = getAssetManager(assetManagerName);
    if (assetManager == null) return null;
    String assetName = name.substring(index + 1);
    Asset asset = assetManager.getAsset(assetName);
    return asset;
  }

  public static void preInit() {
    Compatibility.get().setupAssets();

    if (getAssetManager(CORE) == null) throw new CubesException("No core asset manager");
    if (getAssetManager(CORE).assets.size() == 0) throw new CubesException("No core assets loaded");
  }

  public static void init() {
    packedTextureSheet = getPackedTextureSheet(AssetType.block, AssetType.item);
  }

  private static PackedTextureSheet getPackedTextureSheet(AssetType... assetType) {
    if (Adapter.isDedicatedServer()) return null;
    TexturePacker texturePacker = new TexturePacker(2048, 2048, 1, true);
    for (Map.Entry<String, AssetManager> entry : assetManagers.entrySet()) {
      for (AssetType type : assetType) {
        ArrayList<Asset> assets = entry.getValue().getAssets(type.name() + "/");
        for (Asset asset : assets) {
          try {
            if (!asset.getFileHandle().extension().equals("png")) continue;
            Pixmap pixmap = new Pixmap(asset.getFileHandle());
            texturePacker.insertImage(entry.getKey() + ":" + asset.getPath(), pixmap);
            pixmap.dispose();
          } catch (Exception e) {
            Log.error("Failed to read file: " + asset.getPath(), e);
          }
        }
      }
    }

    Texture texture = new Texture(texturePacker.getPixmap());
    texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    PackedTextureSheet packedTextureSheet = new PackedTextureSheet(new Material(TextureAttribute.createDiffuse(texture)));
    packedTextureSheet.getMaterial().set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));

    Map<String, TexturePacker.PackRectangle> rectangles = texturePacker.getRectangles();
    int num = 0;
    for (Map.Entry<String, TexturePacker.PackRectangle> entry : rectangles.entrySet()) {
      num++;
      PackedTexture packedTexture = new PackedTexture(texture, new TextureRegion(texture, entry.getValue().x, entry.getValue().y, entry.getValue().width, entry.getValue().height));
      packedTextureSheet.getPackedTextures().put(entry.getKey(), packedTexture);
    }
    return packedTextureSheet;
  }

  public static TextureRegion getPackedTexture(String name) {
    PackedTexture packedTexture = packedTextureSheet.getPackedTextures().get(name);
    return packedTexture == null ? null : packedTexture.textureRegion;
  }

  public static TextureRegion getPackedTextureFromID(String id, String type) {
    int index = id.indexOf(":");
    if (index == -1) throw new CubesException("Invalid block id \"" + id + "\"");
    TextureRegion packedTexture = getPackedTexture(id.substring(0, index) + ":" + type + "/" + id.substring(index + 1) + ".png");
    if (packedTexture == null) throw new CubesException("No block texture for " + type + " \"" + id + "\"");
    return packedTexture;
  }
}
