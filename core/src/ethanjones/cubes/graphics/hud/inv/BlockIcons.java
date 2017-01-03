package ethanjones.cubes.graphics.hud.inv;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.block.BlockRenderType;
import ethanjones.cubes.core.id.IDManager;
import ethanjones.cubes.core.util.BlockFace;
import ethanjones.cubes.graphics.Graphics;
import ethanjones.cubes.graphics.world.AreaMesh;
import ethanjones.cubes.graphics.world.BlockTextureHandler;
import ethanjones.cubes.graphics.world.FaceVertices;
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
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class BlockIcons {
  
  private static HashMap<BlockID, TextureRegion> textureRegions = new HashMap<BlockID, TextureRegion>();
  
  public static TextureRegion getIcon(String id, int meta) {
    BlockID blockID = new BlockID();
    blockID.id = id;
    blockID.meta = meta;
    return textureRegions.get(blockID);
  }
  
  public static void renderIcons() {
    int size = 128;
    
    FrameBuffer frameBuffer = new FrameBuffer(Format.RGBA8888, size, size, false);
    frameBuffer.begin();
  
    PerspectiveCamera camera = new PerspectiveCamera(30f, size, size);
    camera.position.set(2.4f, 2f, 2.4f);
    camera.lookAt(0f, 0f, 0f);
    camera.near = 0.1f;
    camera.update(true);
    
  
    final AreaMesh mesh = new AreaMesh();
    List<Block> blocks = IDManager.getBlocks();
    
    int numberBlock = (int) Math.ceil(Math.sqrt(blocks.size()));
    Pixmap pixmap = new Pixmap(size * numberBlock, size * numberBlock, Format.RGBA8888);
    int number = 0;
    HashMap<BlockID, Location> map = new HashMap<BlockID, Location>();
            
    for (Block block : blocks) {
      for (int meta : block.displayMetaValues()) {
        BlockRenderType type = block.renderType(meta);
        if (type != BlockRenderType.DEFAULT) continue;
        BlockTextureHandler textureHandler = block.getTextureHandler(meta);
        int vertexOffset = 0;
        vertexOffset = FaceVertices.createMaxX(Vector3.Zero, textureHandler.getSide(BlockFace.posX), 0, 0, 0, BlockLight.FULL_LIGHT, AreaMesh.vertices, vertexOffset);
        vertexOffset = FaceVertices.createMaxY(Vector3.Zero, textureHandler.getSide(BlockFace.posY), 0, 0, 0, BlockLight.FULL_LIGHT, AreaMesh.vertices, vertexOffset);
        vertexOffset = FaceVertices.createMaxZ(Vector3.Zero, textureHandler.getSide(BlockFace.posZ), 0, 0, 0, BlockLight.FULL_LIGHT, AreaMesh.vertices, vertexOffset);
        mesh.saveVertices(vertexOffset);
        Graphics.modelBatch.begin(camera);
        Graphics.modelBatch.render(new RenderableProvider() {
          @Override
          public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
            renderables.add(mesh.renderable(pool));
          }
        });
        Graphics.modelBatch.end();
        Pixmap bufferPixmap = ScreenUtils.getFrameBufferPixmap(0, 0, size, size);
//        FileHandle fileHandle = Assets.assetsFolder.child(block.id.replace(':', '-') + "-" + meta + ".png");
//        try {
//          PixmapIO.writePNG(fileHandle, bufferPixmap);
//        } catch (GdxRuntimeException e) {
//          Log.error("Failed to write packed image", e);
//        }
        int x = (number % numberBlock) * size;
        int y = (number / numberBlock) * size;
        pixmap.drawPixmap(bufferPixmap, x, y);
        bufferPixmap.dispose();
        BlockID blockID = new BlockID();
        blockID.id = block.id;
        blockID.meta = meta;
        Location l = new Location();
        l.x = x;
        l.y = y;
        map.put(blockID, l);
  
        number++;
        Gdx.gl20.glClearColor(0, 0, 0, 0);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
      }
    }
  
//    FileHandle fileHandle = Assets.assetsFolder.child("blockicons.png");
//    try {
//      PixmapIO.writePNG(fileHandle, pixmap);
//    } catch (GdxRuntimeException e) {
//     Log.error("Failed to write packed image", e);
//   }
  
    Texture texture = new Texture(pixmap, true);
    texture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
  
    for (Entry<BlockID, Location> entry : map.entrySet()) {
      Location l = entry.getValue();
      TextureRegion r = new TextureRegion(texture, l.x, l.y, size, size);
      r.flip(false, true);
      textureRegions.put(entry.getKey(), r);
    }
    
    frameBuffer.end();
    frameBuffer.dispose();
  }
  
  public static class BlockID {
    String id;
    int meta;
  
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
    int x;
    int y;
  }
}
