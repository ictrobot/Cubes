package ethanjones.modularworld.block;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import ethanjones.modularworld.data.ByteData;

/**
 * Setup for Block
 */
public abstract class BlockFactory {
  
  public final String id;
  public int numID;
  
  public BlockFactory(String id) {
    this.id = id;
  }
  
  public abstract void loadGraphics();
  
  public abstract Material getTexture(ByteData data);
  
  public abstract Model getModel(ByteData data);
  
  public Block getBlock(ByteData data) {
    return new Block(this, data);
  }
  
}
