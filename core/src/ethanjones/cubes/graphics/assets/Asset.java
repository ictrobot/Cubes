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
  private Material material;

  protected Asset(AssetManager assetManager, String path, FileHandle fileHandle) {
    this.assetManager = assetManager;
    this.path = path;
    this.fileHandle = fileHandle;
  }
  
  public Texture getTexture() {
    if (texture == null) texture = new Texture(fileHandle);
    return texture;
  }
  
  public TextureRegion getTextureRegion() {
    if (textureRegion == null) textureRegion = new TextureRegion(getTexture());
    return textureRegion;
  }
  
  public Material getMaterial() {
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
    return path;
  }
}
