package ethanjones.cubes.world.light;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.event.EventHandler;
import ethanjones.cubes.core.event.world.block.BlockChangedEvent;
import ethanjones.cubes.world.reference.BlockReference;

public class BlockLightHandler {
  @EventHandler
  public void blockChanged(BlockChangedEvent event) {
    BlockReference blockReference = event.getBlockReference();
    Block oldBlock = event.getOldBlock();
    Block newBlock = event.getNewBlock();

    if (oldBlock != null) {
      if (oldBlock.getLightLevel() > 0) {
        BlockLight.removeLight(blockReference.blockX, blockReference.blockY, blockReference.blockZ);
      } else {
        BlockLight.spreadLight(blockReference.blockX, blockReference.blockY, blockReference.blockZ);
      }
    }
    //TODO remove light if solid block is placed
    if (newBlock != null && newBlock.getLightLevel() > 0) {
      BlockLight.addLight(blockReference.blockX, blockReference.blockY, blockReference.blockZ, event.getNewBlock().getLightLevel());
    }
  }
}
