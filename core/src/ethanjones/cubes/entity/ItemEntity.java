package ethanjones.cubes.entity;

import ethanjones.cubes.core.util.BlockFace;
import ethanjones.cubes.core.util.VectorUtil;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.world.AreaMesh;
import ethanjones.cubes.graphics.world.BlockTextureHandler;
import ethanjones.cubes.graphics.world.FaceVertices;
import ethanjones.cubes.item.ItemBlock;
import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.inv.InventoryHelper;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.data.DataGroup;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import static ethanjones.cubes.world.light.BlockLight.FULL_LIGHT;

public class ItemEntity extends Entity implements RenderableProvider {
  static short[] blockIndices;
  static short[] itemIndices;

  static {
    blockIndices = new short[6 * 6];
    short j = 0;
    for (int i = 0; i < blockIndices.length; i += 6, j += 4) {
      blockIndices[i + 0] = (short) (j + 0);
      blockIndices[i + 1] = (short) (j + 1);
      blockIndices[i + 2] = (short) (j + 2);
      blockIndices[i + 3] = (short) (j + 2);
      blockIndices[i + 4] = (short) (j + 3);
      blockIndices[i + 5] = (short) (j + 0);
    }
    itemIndices = new short[6];
    itemIndices[0] = (short) 0;
    itemIndices[1] = (short) 1;
    itemIndices[2] = (short) 2;
    itemIndices[3] = (short) 2;
    itemIndices[4] = (short) 3;
    itemIndices[5] = (short) 0;
  }

  public ItemStack itemStack;
  public int cooldown;

  Mesh mesh;
  float[] vertices;

  public ItemEntity() {
    super("core:item");
    this.motion.set(MathUtils.random(1f) - 0.5f, 0, MathUtils.random(1f) - 0.5f);
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
    if (mesh == null) {
      if (itemStack.item instanceof ItemBlock) {
        mesh = new Mesh(false, 4 * 6, 6 * 6, AreaMesh.vertexAttributes);
        mesh.setIndices(blockIndices);
        int vertexOffset = 0;
        vertices = new float[AreaMesh.VERTEX_SIZE * 4 * 6];
        BlockTextureHandler textureHandler = ((ItemBlock) itemStack.item).block.getTextureHandler();
        Vector3 offset = new Vector3(-0.5f, 0f, -0.5f);
        vertexOffset = FaceVertices.createMaxX(offset, textureHandler.getSide(BlockFace.posX), 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
        vertexOffset = FaceVertices.createMaxY(offset, textureHandler.getSide(BlockFace.posY), 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
        vertexOffset = FaceVertices.createMaxZ(offset, textureHandler.getSide(BlockFace.posZ), 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
        vertexOffset = FaceVertices.createMinX(offset, textureHandler.getSide(BlockFace.negX), 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
        vertexOffset = FaceVertices.createMinY(offset, textureHandler.getSide(BlockFace.negY), 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
        vertexOffset = FaceVertices.createMinZ(offset, textureHandler.getSide(BlockFace.negZ), 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
        mesh.setVertices(vertices);
      } else {
        mesh = new Mesh(false, 4, 6, AreaMesh.vertexAttributes);
        mesh.setIndices(itemIndices);
        vertices = new float[AreaMesh.VERTEX_SIZE * 4];
        TextureRegion textureRegion = itemStack.item.getTextureRegion();
        Vector3 offset = new Vector3(-0.5f, 0f, 0);
        FaceVertices.createMinZ(offset, textureRegion, 0, 0, 0, FULL_LIGHT, vertices, 0);
        mesh.setVertices(vertices);
      }
    }
    Renderable renderable = new Renderable();
    renderable.worldTransform.translate(position.x, position.y + yOffset(), position.z);
    renderable.worldTransform.scl(0.3f);
    renderable.worldTransform.rotate(Vector3.Y, (System.currentTimeMillis() % 7200) / 20);
    renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
    renderable.meshPart.offset = 0;
    renderable.meshPart.size = 6 * 6;
    renderable.meshPart.mesh = mesh;
    renderable.material = Assets.packedTextureSheet.getMaterial();
    renderables.add(renderable);
  }

  private static float yOffset() {
    long l = System.currentTimeMillis();
    l %= 2000;
    if (l > 1000) l = 2000 - l;
    float f = (float) l / 8000f;
    return f;
  }

  @Override
  public void dispose() {
    super.dispose();
    if (mesh != null) mesh.dispose();
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
