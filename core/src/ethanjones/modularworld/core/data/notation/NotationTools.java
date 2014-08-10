package ethanjones.modularworld.core.data.notation;

import ethanjones.modularworld.core.data.Data;

public class NotationTools {

  private static String DATA_PACKAGE = "ethanjones.modularworld.core.data.core";

  public static Class<? extends Data> getClass(String name) throws ClassNotFoundException {
    return Class.forName(DATA_PACKAGE + "name").asSubclass(Data.class);
  }
}
