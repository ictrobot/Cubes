package ethanjones.cubes.entity;

import ethanjones.cubes.core.util.VectorUtil;
import ethanjones.cubes.graphics.entity.ItemEntityRenderer;
import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.inv.InventoryHelper;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.data.DataGroup;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class ItemEntity extends Entity implements RenderableProvider {

  public ItemStack itemStack;
  public int cooldown;
  private ItemEntityRenderer renderer;

  public ItemEntity() {
    super("core:item");
    this.motion.set(MathUtils.random(1f) - 0.5f, 0, MathUtils.random(1f) - 0.5f);
    if (Sided.getSide() == Side.Client) renderer = new ItemEntityRenderer(this);
  }

  @Override
  public DataGroup write() {
    DataGroup write = super.write();
    write.put("itemstack", itemStack.write());
    return write;
  }

  @Override
  public void read(DataGroup data) {
    super.read(data);
    itemStack = new ItemStack();
    itemStack.read(data.getGroup("itemstack"));
  }

  @Override
  public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
    renderer.getRenderables(renderables, pool);
  }

  @Override
  public void dispose() {
    super.dispose();
    if (renderer != null) renderer.dispose();
  }

  public boolean update() {
    super.update();
    if (Sided.getSide() == Side.Server) {
      if (cooldown > 0) {
        cooldown--;
      } else {
        for (ClientIdentifier clientIdentifier : Cubes.getServer().getAllClients()) {
          float distance2 = VectorUtil.distance2(this.position, clientIdentifier.getPlayer().position.cpy().sub(0, clientIdentifier.getPlayer().height, 0));
          if (distance2 < 1f) {
            InventoryHelper.addItemstack(clientIdentifier.getPlayer().getInventory(), itemStack);
            return true;
          }
        }
      }
    }
    return false;
  }
}
