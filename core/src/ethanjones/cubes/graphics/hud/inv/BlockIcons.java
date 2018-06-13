package ethanjones.cubes.graphics.hud.inv;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.id.IDManager;
import ethanjones.cubes.core.util.BlockFace;
import ethanjones.cubes.graphics.CubesVertexAttributes;
import ethanjones.cubes.graphics.Graphics;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.world.area.AreaMesh;
import ethanjones.cubes.graphics.world.block.BlockRenderType;
import ethanjones.cubes.graphics.world.block.BlockTextureHandler;
import ethanjones.cubes.graphics.world.block.FaceVertices;
import ethanjones.cubes.world.light.BlockLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class BlockIcons {

  private static HashMap<BlockID, TextureRegion> textureRegions = new HashMap<BlockID, TextureRegion>();

  public static TextureRegion getIcon(String id, int meta) {
    return textureRegions.get(new BlockID(id, meta));
  }

  public static void renderIcons() {
    int size = 64;

    FrameBuffer frameBuffer = new FrameBuffer(Format.RGBA8888, size, size, false);
    frameBuffer.begin();

    PerspectiveCamera camera = new PerspectiveCamera(30f, size, size);
    camera.position.set(2.4f, 2f, 2.4f);
    camera.lookAt(0f, 0f, 0f);
    camera.near = 0.1f;
    camera.up.set(0, -1, 0);
    camera.update(true);

    final AreaMesh mesh = new AreaMesh(CubesVertexAttributes.VERTEX_ATTRIBUTES);
    List<Block> blocks = IDManager.getBlocks();

    ArrayList<BlockID> blockids = new ArrayList<BlockID>();
    for (Block block : blocks) {
      for (int meta : block.displayMetaValues()) {
        if (block.renderType(meta) != BlockRenderType.DEFAULT) continue;
        blockids.add(new BlockID(block.id, meta));
      }
    }

    int numberBlock = (int) Math.ceil(Math.sqrt(blockids.size()));
    Pixmap pixmap = new Pixmap(size * numberBlock, size * numberBlock, Format.RGBA8888);
    int number = 0;
    HashMap<BlockID, Location> map = new HashMap<BlockID, Location>();

    mesh.renderable.setLightOverride(BlockLight.FULL_LIGHT);
    mesh.renderable.setFogEnabled(false);
    mesh.renderable.name = "AreaMesh BlockIcons";

    for (BlockID blockID : blockids) {
      Block block = IDManager.toBlock(blockID.id);
      BlockTextureHandler textureHandler = block.getTextureHandler(blockID.meta);
      int vertexOffset = 0;
      vertexOffset = FaceVertices.createMaxX(Vector3.Zero, textureHandler.getSide(BlockFace.posX), null, 0, 0, 0, BlockLight.FULL_LIGHT, AreaMesh.vertices, vertexOffset);
      vertexOffset = FaceVertices.createMaxY(Vector3.Zero, textureHandler.getSide(BlockFace.posY), null, 0, 0, 0, BlockLight.FULL_LIGHT, AreaMesh.vertices, vertexOffset);
      vertexOffset = FaceVertices.createMaxZ(Vector3.Zero, textureHandler.getSide(BlockFace.posZ), null, 0, 0, 0, BlockLight.FULL_LIGHT, AreaMesh.vertices, vertexOffset);
      mesh.saveVertices(vertexOffset);

      Graphics.modelBatch.begin(camera);
      Graphics.modelBatch.render(mesh.renderable);
      Graphics.modelBatch.end();
      Pixmap bufferPixmap = ScreenUtils.getFrameBufferPixmap(0, 0, size, size);

      int x = (number % numberBlock) * size;
      int y = (number / numberBlock) * size;
      pixmap.drawPixmap(bufferPixmap, x, y);
      bufferPixmap.dispose();
      map.put(blockID, new Location(x, y));

      number++;
      Gdx.gl20.glClearColor(0, 0, 0, 0);
      Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }

    mesh.dispose();

    Texture texture = new Texture(pixmap, false);
    texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

    for (Entry<BlockID, Location> entry : map.entrySet()) {
      Location l = entry.getValue();
      textureRegions.put(entry.getKey(), new TextureRegion(texture, l.x, l.y, size, size));
    }

    frameBuffer.end();
    frameBuffer.dispose();
  }

  public static class BlockID {

    final String id;
    final int meta;

    public BlockID(String id, int meta) {
      this.id = id;
      this.meta = meta;
    }

    @Override
    public int hashCode() {
      return (31 * id.hashCode()) + meta;
    }

    @Override
    public boolean equals(Object obj) {
      return obj instanceof BlockID && id.equals(((BlockID) obj).id) && meta == ((BlockID) obj).meta;
    }
  }

  private static class Location {

    final int x;
    final int y;

    private Location(int x, int y) {
      this.x = x;
      this.y = y;
    }
  }
}
