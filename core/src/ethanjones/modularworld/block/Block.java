package ethanjones.modularworld.block;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import ethanjones.modularworld.core.data.DataGroup;
import ethanjones.modularworld.graphics.world.BlockTextureHandler;

public abstract class Block {
  private static BoundingBox fullBlockBoundingBox = new BoundingBox(new Vector3(0, 0, 0), new Vector3(1, 1, 1));

  protected BlockTextureHandler textureHandler;
  String mainMaterial;

  public Block(String mainMaterial) {
    this.mainMaterial = mainMaterial;
  }

  public void loadGraphics() {
    textureHandler = new BlockTextureHandler(mainMaterial);
  }

  public BlockTextureHandler getTextureHandler(DataGroup data) {
    return textureHandler;
  }

  public BoundingBox getBoundingBox() {
    return fullBlockBoundingBox;
  }

}
