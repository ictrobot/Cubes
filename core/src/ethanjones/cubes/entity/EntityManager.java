package ethanjones.cubes.entity;

import ethanjones.cubes.entity.living.player.Player;

import java.util.*;

public class EntityManager {

  private static List<Class<? extends Entity>> classList = new ArrayList<Class<? extends Entity>>();
  private static Map<String, Class<? extends Entity>> idToClass = new HashMap<String, Class<? extends Entity>>();
  private static Map<Class<? extends Entity>, String> classToId = new HashMap<Class<? extends Entity>, String>();

  static {
    register(ItemEntity.class, "core:item");
    register(Player.class, "core:player");
  }

  public static void register(Class<? extends Entity> c, String id) {
    if (c == null) return;
    classList.add(c);
    idToClass.put(id, c);
    classToId.put(c, id);
  }

  public static Class<? extends Entity> toClass(String id) {
    if (id == null || id.isEmpty()) return null;
    return idToClass.get(id);
  }


  public static String toID(Class<? extends Entity> c) {
    if (c == null) return null;
    return classToId.get(c);
  }

  public static List<Class<? extends Entity>> getClasses() {
    return classList;
  }

  public static void loaded() {
    classList = Collections.unmodifiableList(classList);
    idToClass = Collections.unmodifiableMap(idToClass);
    classToId = Collections.unmodifiableMap(classToId);
  }
}
