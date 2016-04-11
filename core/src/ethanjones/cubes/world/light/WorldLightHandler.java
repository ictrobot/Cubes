package ethanjones.cubes.world.light;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.event.EventHandler;
import ethanjones.cubes.core.event.world.block.BlockChangedEvent;
import ethanjones.cubes.world.reference.BlockReference;

public class WorldLightHandler {
  @EventHandler
  public void blockChanged(BlockChangedEvent event) {
    BlockReference blockReference = event.getBlockReference();
    Block oldBlock = event.getOldBlock();
    Block newBlock = event.getNewBlock();

    // Block light
    BlockLight.removeLight(blockReference.blockX, blockReference.blockY, blockReference.blockZ);
    if (newBlock != null && newBlock.getLightLevel() > 0) {
      BlockLight.addLight(blockReference.blockX, blockReference.blockY, blockReference.blockZ, event.getNewBlock().getLightLevel());
    }
    BlockLight.spreadLight(blockReference.blockX, blockReference.blockY, blockReference.blockZ);
    // Sunlight
    if (newBlock != null) {
      SunLight.removeSunlight(blockReference.blockX, blockReference.blockY, blockReference.blockZ);
    } else {
      SunLight.addSunlight(blockReference.blockX, blockReference.blockY, blockReference.blockZ);
    }
  }
}
