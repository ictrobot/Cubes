package ethanjones.cubes.block;

import ethanjones.cubes.graphics.world.BlockTextureHandler;
import ethanjones.cubes.world.storage.Area;

import com.badlogic.gdx.math.Vector3;

public class BlockRenderType {
  
  public static final BlockRenderType DEFAULT = new BlockRenderType(false);
  public static final BlockRenderType CROSS = new BlockRenderType(false);
  public static final BlockRenderType CROSS_STRETCHED = new BlockRenderType(false);
  
  public final boolean custom;
  
  public BlockRenderType() {
    this(true);
  }
  
  private BlockRenderType(boolean custom) {
    this.custom = custom;
  }
  
  public int render(float[] vertices, int vertexOffset, Vector3 areaOffset, Block block, int meta, BlockTextureHandler textureHandler, Area area, int x, int y, int z) {
    return vertexOffset;
  }
}
