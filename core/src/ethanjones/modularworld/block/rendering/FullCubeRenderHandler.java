package ethanjones.modularworld.block.rendering;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.core.util.Direction;
import ethanjones.modularworld.graphics.GameModel;
import ethanjones.modularworld.graphics.GraphicsHelper;
import ethanjones.modularworld.world.World;

public class FullCubeRenderHandler implements BlockRenderHandler {

  public FullCubeRenderHandler(Material mainMaterial) {
    for (int i = 0; i <= 5; i++) {
      materials[i] = mainMaterial; //i + ""
    }
  }


  Material[] materials = new Material[6];

  private Material getSide(Direction direction) {
    return materials[direction.index];
  }

  public FullCubeRenderHandler setSide(Direction direction, Material material) {
    materials[direction.index] = material;
    return this;
  }

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
  public int render(ModelBatch modelBatch, Environment environment, Camera camera, int x, int y, int z) {

    if (!camera.frustum.boundsInFrustum(x + 0.5f, y + 0.5f, z + 0.5f, 0.5f, 0.5f, 0.5f)) {
      return 0;
    }

    World world = ModularWorld.instance.world;
    int num = 0;

    if (world.getBlock(x + 1, y, z) == null) {
      modelBatch.render(posX.setPos(x, y, z), environment);
      num++;
    }
    if (world.getBlock(x - 1, y, z) == null) {
      modelBatch.render(negX.setPos(x, y, z), environment);
      num++;
    }

    if (y != World.HEIGHT_LIMIT && world.getBlock(x, y + 1, z) == null) {
      modelBatch.render(posY.setPos(x, y, z), environment);
      num++;
    }

    if (y != 0 && world.getBlock(x, y - 1, z) == null) {
      modelBatch.render(negY.setPos(x, y, z), environment);
      num++;
    }

    if (world.getBlock(x, y, z + 1) == null) {
      modelBatch.render(posZ.setPos(x, y, z), environment);
      num++;
    }
    if (world.getBlock(x, y, z - 1) == null) {
      modelBatch.render(negZ.setPos(x, y, z), environment);
      num++;
    }

    return num;
  }
}
