package ethanjones.modularworld.graphics.world.block;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public interface BlockRenderer extends Disposable {

  public void getRenderables(Array<Renderable> renderables);
}
