package ethanjones.cubes.world.light;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.event.EventHandler;
import ethanjones.cubes.core.event.world.block.BlockChangedEvent;
import ethanjones.cubes.core.id.TransparencyManager;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.performance.Performance;
import ethanjones.cubes.core.performance.PerformanceTags;
import ethanjones.cubes.world.reference.BlockReference;
import ethanjones.cubes.world.storage.Area;
import ethanjones.cubes.world.thread.AreaNotLoadedException;

public class WorldLightHandler {
  @EventHandler(critical = true)
  public void blockChanged(BlockChangedEvent event) {
    BlockReference blockReference = event.getBlockReference();
    Block oldBlock = event.getOldBlock();
    Block newBlock = event.getNewBlock();
    int oldMeta = event.getOldMeta();
    int newMeta = event.getNewMeta();

    if ((oldBlock == null ? 0 : oldBlock.getLightLevel(oldMeta)) == (newBlock == null ? 0 : newBlock.getLightLevel(newMeta)) && TransparencyManager.isTransparent(oldBlock, oldMeta) == TransparencyManager.isTransparent(newBlock, newMeta)) {
      return;
    }

    Performance.start(PerformanceTags.LIGHT_UPDATE);
    Area area = event.getArea();
    LightWorldSection lws = new LightWorldSection(area);
    try {
      // Block light
      BlockLight.removeLight(blockReference.blockX, blockReference.blockY, blockReference.blockZ, area, lws);
      if (newBlock != null && newBlock.getLightLevel(newMeta) > 0) {
        BlockLight.addLight(blockReference.blockX, blockReference.blockY, blockReference.blockZ, event.getNewBlock().getLightLevel(newMeta), area, lws);
      }
      BlockLight.spreadLight(blockReference.blockX, blockReference.blockY, blockReference.blockZ, area, lws);
      // Sunlight
      if (newBlock != null && !newBlock.isTransparent(newMeta)) {
        SunLight.removeSunlight(blockReference.blockX, blockReference.blockY, blockReference.blockZ, area, lws);
      } else {
        SunLight.addSunlight(blockReference.blockX, blockReference.blockY, blockReference.blockZ, area, lws);
      }
    } catch (AreaNotLoadedException e) {
      Log.error("Failed to update light", e);
    }
    lws.unlock();
    Performance.stop(PerformanceTags.LIGHT_UPDATE);
  }
}
