package ethanjones.cubes.graphics.assets;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;

public class Asset {

  private final AssetManager assetManager;
  private final String path;
  private final FileHandle fileHandle;
  
  private Texture texture;
  private TextureRegion textureRegion;
  private TextureRegion packedTextureRegion;
  private PackedTextureSheet packedTextureSheet;
  private Material material;

  protected Asset(AssetManager assetManager, String path, FileHandle fileHandle) {
    this.assetManager = assetManager;
    this.path = path.replace('\\', '/');
    this.fileHandle = fileHandle;
  }
  
  public Texture getTexture() {
    if (texture == null) texture = new Texture(fileHandle);
    return texture;
  }
  
  public TextureRegion getTextureRegion() {
    if (packedTextureRegion != null) return packedTextureRegion;
    if (textureRegion == null) textureRegion = new TextureRegion(getTexture());
    return textureRegion;
  }

  public TextureRegion getOwnTextureRegion() {
    if (textureRegion == null) textureRegion = new TextureRegion(getTexture());
    return textureRegion;
  }

  void setPackedTextureRegion(TextureRegion packedTextureRegion, PackedTextureSheet packedTextureSheet) {
    this.packedTextureRegion = packedTextureRegion;
    this.packedTextureSheet = packedTextureSheet;
  }

  public TextureRegion getPackedTextureRegion() {
    return packedTextureRegion;
  }
  
  public Material getMaterial() {
    if (packedTextureSheet != null) return packedTextureSheet.getMaterial();
    if (material == null) material = new Material(TextureAttribute.createDiffuse(getTexture()));
    return material;
  }
  
  public AssetManager getAssetManager() {
    return assetManager;
  }

  public String getPath() {
    return path;
  }

  public FileHandle getFileHandle() {
    return fileHandle;
  }

  public String toString() {
    return assetManager.getName() + ":" + path;
  }
}
