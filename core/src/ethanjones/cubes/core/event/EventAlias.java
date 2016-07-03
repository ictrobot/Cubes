package ethanjones.cubes.core.event;

import ethanjones.cubes.core.system.CubesException;

import java.util.HashMap;

public class EventAlias {
  private static final HashMap<String, Class<? extends Event>> map = new HashMap<String, Class<? extends Event>>();

  public static Class<? extends Event> getEventClass(String s) {
    try {
      Class<?> c = Class.forName(s);
      if (c.isAssignableFrom(Event.class)) return c.asSubclass(Event.class);
    } catch (ClassNotFoundException ignored) {
    }
    if (map.containsKey(s)) return map.get(s);
    throw new CubesException("No such event '" + s + "'");
  }

  public static void registerAlias(String alias, Class<? extends Event> c) {
    map.put(alias, c);
  }

  static {
    registerAlias("PlayerMovementEvent", ethanjones.cubes.core.event.entity.living.player.PlayerMovementEvent.class);
    registerAlias("PlayerPlaceBlockEvent", ethanjones.cubes.core.event.entity.living.player.PlayerPlaceBlockEvent.class);

    registerAlias("BlockChangedEvent", ethanjones.cubes.core.event.world.block.BlockChangedEvent.class);

    registerAlias("AreaLoadedEvent", ethanjones.cubes.core.event.world.generation.AreaLoadedEvent.class);
    registerAlias("AreaFeaturesEvent", ethanjones.cubes.core.event.world.generation.FeaturesEvent.class);
    registerAlias("AreaGenerationEvent", ethanjones.cubes.core.event.world.generation.GenerationEvent.class);

    registerAlias("SaveEvent", ethanjones.cubes.core.event.world.save.SaveEvent.class);
  }
}
