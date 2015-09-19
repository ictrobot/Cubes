package ethanjones.cubes.world.client;

import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.reference.multi.MultiAreaReference;
import ethanjones.cubes.world.storage.Area;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class WorldClient extends World {

  private ArrayList<Area> removed = new ArrayList<Area>();
  private AreaReference playerArea = new AreaReference();
  private final int renderDistance = Settings.getIntegerSettingValue(Settings.GRAPHICS_VIEW_DISTANCE) + 3; //keep 3 extra

  public WorldClient() {
    super(null);
  }

  @Override
  public void tick() {
    super.tick();

    playerArea.setFromPositionVector3(Cubes.getClient().player.position);
    removed.clear();

    lock.writeLock();
    Iterator<Map.Entry<AreaReference, Area>> iterator = map.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<AreaReference, Area> entry = iterator.next();
      Area area = entry.getValue();
      if (Math.abs(area.areaX - playerArea.areaX) > renderDistance || Math.abs(area.areaZ - playerArea.areaZ) > renderDistance) {
        removed.add(area);
        iterator.remove();
      }
    }
    lock.writeUnlock();

    for (Area area : removed) {
      area.unload();
    }
  }

  @Override
  public void dispose() {
    super.dispose();
  }

  //TODO implement below
  @Override
  public void requestRegion(MultiAreaReference references) {

  }
}
