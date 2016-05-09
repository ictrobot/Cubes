package ethanjones.cubes.graphics.entity;

import ethanjones.cubes.core.util.BlockFace;
import ethanjones.cubes.entity.ItemEntity;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.world.AreaMesh;
import ethanjones.cubes.graphics.world.BlockTextureHandler;
import ethanjones.cubes.graphics.world.FaceVertices;
import ethanjones.cubes.item.Item;
import ethanjones.cubes.item.ItemBlock;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.light.LightNode;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;

import static ethanjones.cubes.world.light.BlockLight.FULL_LIGHT;

public class ItemEntityRenderer implements RenderableProvider, Disposable {
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

    itemIndices = new short[6 * 2];
    j = 0;
    for (int i = 0; i < itemIndices.length; i += 6, j += 4) {
      itemIndices[i + 0] = (short) (j + 0);
      itemIndices[i + 1] = (short) (j + 1);
      itemIndices[i + 2] = (short) (j + 2);
      itemIndices[i + 3] = (short) (j + 2);
      itemIndices[i + 4] = (short) (j + 3);
      itemIndices[i + 5] = (short) (j + 0);
    }
  }

  private final ItemEntity itemEntity;
  private Mesh mesh;
  private float[] vertices;
  private Item item;
  private float randomYOffset = (float) (Math.random() / 20d);

  public ItemEntityRenderer(ItemEntity itemEntity) {
    this.itemEntity = itemEntity;
  }

  @Override
  public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
    if (itemEntity.itemStack.item != item) {
      if (mesh != null) mesh.dispose();
      mesh = null;
    }

    if (mesh == null) {
      item = itemEntity.itemStack.item;
      if (item instanceof ItemBlock) {
        mesh = new Mesh(false, 4 * 6, 6 * 6, AreaMesh.vertexAttributes);
        mesh.setIndices(blockIndices);
        int vertexOffset = 0;
        vertices = new float[AreaMesh.VERTEX_SIZE * 4 * 6];
        BlockTextureHandler textureHandler = ((ItemBlock) itemEntity.itemStack.item).block.getTextureHandler();
        Vector3 offset = new Vector3(-0.5f, 0f, -0.5f);
        vertexOffset = FaceVertices.createMaxX(offset, textureHandler.getSide(BlockFace.posX), 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
        vertexOffset = FaceVertices.createMaxY(offset, textureHandler.getSide(BlockFace.posY), 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
        vertexOffset = FaceVertices.createMaxZ(offset, textureHandler.getSide(BlockFace.posZ), 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
        vertexOffset = FaceVertices.createMinX(offset, textureHandler.getSide(BlockFace.negX), 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
        vertexOffset = FaceVertices.createMinY(offset, textureHandler.getSide(BlockFace.negY), 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
        vertexOffset = FaceVertices.createMinZ(offset, textureHandler.getSide(BlockFace.negZ), 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
        mesh.setVertices(vertices);
      } else {
        mesh = new Mesh(false, 4 * 2, 6 * 2, AreaMesh.vertexAttributes);
        mesh.setIndices(itemIndices);
        int vertexOffset = 0;
        vertices = new float[AreaMesh.VERTEX_SIZE * 4 * 2];
        TextureRegion textureRegion = itemEntity.itemStack.item.getTextureRegion();
        TextureRegion flip = new TextureRegion(textureRegion);
        flip.flip(true, false);
        vertexOffset = FaceVertices.createMinZ(new Vector3(-0.5f, 0f, 0f), textureRegion, 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
        vertexOffset = FaceVertices.createMaxZ(new Vector3(-0.5f, 0f, -1f), flip, 0, 0, 0, FULL_LIGHT, vertices, vertexOffset);
        mesh.setVertices(vertices);
      }
    }
    Renderable renderable = new Renderable();
    renderable.worldTransform.translate(itemEntity.position.x, itemEntity.position.y + yOffset() + randomYOffset, itemEntity.position.z);
    renderable.worldTransform.scl(0.3f);
    renderable.worldTransform.rotate(Vector3.Y, (System.currentTimeMillis() % 7200) / 20);
    renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
    renderable.meshPart.offset = 0;
    renderable.meshPart.size = item instanceof ItemBlock ? 6 * 6 : 6 * 2;
    renderable.meshPart.mesh = mesh;
    renderable.material = Assets.packedTextureSheet.getMaterial();

    LightNode lightNode = new LightNode(CoordinateConverter.block(itemEntity.position.x), CoordinateConverter.block(itemEntity.position.y), CoordinateConverter.block(itemEntity.position.z), 0);
    lightNode.l = Cubes.getClient().world.getLightRaw(lightNode.x, lightNode.y, lightNode.z);

    renderable.userData = lightNode;
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
    if (mesh != null) {
      mesh.dispose();
      mesh = null;
    }
  }
}
