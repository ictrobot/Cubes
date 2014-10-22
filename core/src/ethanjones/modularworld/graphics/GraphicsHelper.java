package ethanjones.modularworld.graphics;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import ethanjones.modularworld.core.compatibility.Compatibility;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.core.system.ModularWorldException;
import ethanjones.modularworld.graphics.asset.AssetManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GraphicsHelper {

  public static final int attributes = Usage.Position | Usage.Normal | Usage.TextureCoordinates;
  public static final VertexAttributes vertexAttributes = MeshBuilder.createAttributes(attributes);
  public static AssetManager assetManager;
  private static Array<TexturePacker> texturePackers = new Array<TexturePacker>();
  private static Array<Material> packedMaterials;
  private static HashMap<String, PackedTexture> textures = new HashMap<String, PackedTexture>();
  private static PackedTexture.PackedMaterial blockPackedTextures;

  public static PackedTexture getTexture(String name) {
    PackedTexture packedTextureWrapper = textures.get(stringToHashMap(name));
    if (packedTextureWrapper == null || packedTextureWrapper.packedTexture == null) {
      Log.error(new ModularWorldException("No such texture: " + name + " in map: " + Character.LINE_SEPARATOR + textures.toString()));
    }
    return packedTextureWrapper;
  }

  public static BitmapFont getFont() {
    return new BitmapFont(assetManager.assets.folders.get("font").files.get("font.fnt").fileHandle);
  }

  public static PackedTexture getBlockTexture(String name) {
    PackedTexture packedTexture = getTexture("blocks/" + name + ".png");
    if (!packedTexture.material.equals(getBlockTextureSheet())) {
      Log.error(new ModularWorldException("Block textures have to be on block packed texture"));
    }
    return packedTexture;
  }

  public static Material getBlockTextureSheet() {
    return blockPackedTextures;
  }

  public static void init(AssetManager assetManager) {
    GraphicsHelper.assetManager = assetManager;
    FileHandle parent = Compatibility.get().getBaseFolder().child("PackedTextures");
    parent.deleteDirectory();
    parent.mkdirs();
    AssetManager.AssetFolder assetFolder = assetManager.assets;
    Array<AssetManager.Asset> textureHandles = new Array<AssetManager.Asset>();

    AssetManager.AssetFolder blockFolder = assetManager.assets.folders.get("blocks");
    Array<AssetManager.Asset> blockTextureHandles = new Array<AssetManager.Asset>();

    findTexture(blockFolder, new Array<AssetManager.AssetFolder>(), blockTextureHandles);
    pack(blockTextureHandles);
    if (texturePackers.size > 1) {
      Log.error(new ModularWorldException("Only one sheet of block textures is allowed"));
    }
    Array<AssetManager.AssetFolder> ignore = new Array<AssetManager.AssetFolder>();
    ignore.add(blockFolder);
    ignore.add(assetFolder.folders.get("font"));
    findTexture(assetFolder, ignore, textureHandles);
    pack(textureHandles);

    packedMaterials = new Array<Material>(texturePackers.size);
    for (int i = 0; i < texturePackers.size; i++) {
      TexturePacker texturePacker = texturePackers.get(i);

      String filename = i + ".png";
      FileHandle fileHandle = parent.child(filename);

      try {
        PixmapIO.writePNG(fileHandle, texturePacker.getPixmap());
      } catch (GdxRuntimeException e) {
        Log.error("Failed to write packed image", e);
      }

      Texture texture = new Texture(fileHandle);
      texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
      PackedTexture.PackedMaterial material = new PackedTexture.PackedMaterial(TextureAttribute.createDiffuse(texture));
      packedMaterials.add(material);
      if (i == 0) {
        blockPackedTextures = material;
      }

      Map<String, TexturePacker.PackRectangle> rectangles = texturePacker.getRectangles();
      int num = 0;
      for (String str : rectangles.keySet()) {
        num++;
        TexturePacker.PackRectangle rectangle = rectangles.get(str);
        str = stringToHashMap(str);
        textures.put(str, new PackedTexture(texture, num, material, new TextureRegion(texture, rectangle.x, rectangle.y, rectangle.width, rectangle.height), str));
      }
    }
  }

  private static String stringToHashMap(String str) {
    return str.replace("\\", "/");
  }

  private static void pack(Array<AssetManager.Asset> files) {
    TexturePacker texturePacker = getTexturePacker();
    for (AssetManager.Asset asset : files) {
      try {
        if (!addToTexturePacker(texturePacker, asset)) {
          texturePackers.add(texturePacker);
          texturePacker = getTexturePacker();
          addToTexturePacker(texturePacker, asset);
        }
      } catch (IOException e) {
        Log.error("Failed to read file: " + asset.path, e);
      }
    }
    if (texturePacker.getRectangles().size() != 0) texturePackers.add(texturePacker);
  }

  private static boolean addToTexturePacker(TexturePacker texturePacker, AssetManager.Asset asset) throws IOException {
    return texturePacker.insertImage(asset.path, new Pixmap(asset.bytes, 0, asset.bytes.length));
  }

  private static TexturePacker getTexturePacker() {
    return new TexturePacker(2048, 2048, 0);
  }

  private static void findTexture(AssetManager.AssetFolder parent, Array<AssetManager.AssetFolder> exclude, Array<AssetManager.Asset> files) {
    if (exclude.contains(parent, false)) return;
    for (AssetManager.AssetFolder folder : parent.folders.values()) {
      findTexture(folder, exclude, files);
    }
    for (AssetManager.Asset file : parent.files.values()) {
      if (file.path.endsWith(".png")) files.add(file);
    }
  }

}
