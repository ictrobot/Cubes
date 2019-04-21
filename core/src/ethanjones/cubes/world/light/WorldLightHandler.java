package ethanjones.cubes.world.light;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.event.EventHandler;
import ethanjones.cubes.core.event.world.block.BlockChangedEvent;
import ethanjones.cubes.core.id.TransparencyManager;
import ethanjones.cubes.core.logging.Log;
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

    Area area = event.getArea();
    try (LightWorldSection lws = new LightWorldSection(area)) {
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
  }
}
