package ethanjones.modularworld.block.rendering;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.core.util.Direction;
import ethanjones.modularworld.graphics.GameModel;
import ethanjones.modularworld.graphics.GraphicsHelper;
import ethanjones.modularworld.world.World;
import ethanjones.modularworld.world.rendering.RenderArea;

public class FullCubeRenderHandler implements BlockRenderHandler {

  Material[] materials = new Material[6];
  Model mPosX;
  Model mNegX;
  Model mPosY;
  Model mNegY;
  Model mPosZ;
  Model mNegZ;
  GameModel posX;
  GameModel negX;
  GameModel posY;
  GameModel negY;
  GameModel posZ;
  GameModel negZ;

  public FullCubeRenderHandler(Material mainMaterial) {
    for (int i = 0; i <= 5; i++) {
      materials[i] = mainMaterial; //i + ""
    }
  }

  private Material getSide(Direction direction) {
    return materials[direction.index];
  }

  public FullCubeRenderHandler setSide(Direction direction, Material material) {
    materials[direction.index] = material;
    return this;
  }

  @Override
  public void load() {
    mPosX = GraphicsHelper.getModelBuilder().createRect(1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, getSide(Direction.posX), GraphicsHelper.usage);
    mNegX = GraphicsHelper.getModelBuilder().createRect(0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, getSide(Direction.negX), GraphicsHelper.usage);
    mPosY = GraphicsHelper.getModelBuilder().createRect(1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0, getSide(Direction.posY), GraphicsHelper.usage);
    mNegY = GraphicsHelper.getModelBuilder().createRect(0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, getSide(Direction.negY), GraphicsHelper.usage);
    mPosZ = GraphicsHelper.getModelBuilder().createRect(0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 0, 1, getSide(Direction.posZ), GraphicsHelper.usage);
    mNegZ = GraphicsHelper.getModelBuilder().createRect(1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 1, 1, 0, 0, 1, getSide(Direction.negZ), GraphicsHelper.usage);

    posX = new GameModel(mPosX);
    negX = new GameModel(mNegX);
    posY = new GameModel(mPosY);
    negY = new GameModel(mNegY);
    posZ = new GameModel(mPosZ);
    negZ = new GameModel(mNegZ);
  }

  @Override
  public void render(RenderArea renderArea, Camera camera, int x, int y, int z) {
    World world = ModularWorld.instance.world;

    if (world.getBlock(x + 1, y, z) == null) {
      renderArea.add(posX.setPos(x, y, z));
    }
    if (world.getBlock(x - 1, y, z) == null) {
      renderArea.add(negX.setPos(x, y, z));
    }

    if (y != World.HEIGHT_LIMIT && world.getBlock(x, y + 1, z) == null) {
      renderArea.add(posY.setPos(x, y, z));
    }
    if (y != 0 && world.getBlock(x, y - 1, z) == null) {
      renderArea.add(negX.setPos(x, y, z));
    }

    if (world.getBlock(x, y, z + 1) == null) {
      renderArea.add(posZ.setPos(x, y, z));
    }
    if (world.getBlock(x, y, z - 1) == null) {
      renderArea.add(negZ.setPos(x, y, z));
    }
  }
}
