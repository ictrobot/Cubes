package ethanjones.cubes.world.light;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.event.EventHandler;
import ethanjones.cubes.core.event.world.block.BlockChangedEvent;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.world.reference.BlockReference;
import ethanjones.cubes.world.thread.AreaNotLoadedException;

public class WorldLightHandler {
  @EventHandler(critical = true)
  public void blockChanged(BlockChangedEvent event) {
    BlockReference blockReference = event.getBlockReference();
    Block oldBlock = event.getOldBlock();
    Block newBlock = event.getNewBlock();

    try {
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
    } catch (AreaNotLoadedException e) {
      Log.error("Failed to update light", e);
    }
  }
}
