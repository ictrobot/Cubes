package ethanjones.cubes.core.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.HashMap;

public class Branding {

  public final static String NAME;
  public final static String AUTHOR;
  public final static String PLATFORM;

  public final static String VERSION_HASH;
  public final static int VERSION_MAJOR;
  public final static int VERSION_MINOR;
  public final static int VERSION_POINT;
  public final static int VERSION_BUILD;
  public final static String VERSION_FULL;
  public final static String VERSION_MAJOR_MINOR_POINT;
  public final static String VERSION;

  public final static boolean IS_DEBUG;
  public final static String DEBUG;

  static {
    try {
      NAME = "Cubes Minimized";
      AUTHOR = "Ethan Jones";
      PLATFORM = Gdx.app.getType().name();

      HashMap<String, String> properties = map(getFile("version"));
      FileHandle buildFile = getFile("build");
      if (buildFile.exists()) {
        HashMap<String, String> buildProperties = map(buildFile);
        if (buildProperties.get("build") != null) {
          properties.put("build", buildProperties.get("build"));
          properties.put("hash", buildProperties.get("hash"));
        }
      }

      VERSION_MAJOR = Integer.parseInt(properties.get("major"));
      VERSION_MINOR = Integer.parseInt(properties.get("minor"));
      VERSION_POINT = Integer.parseInt(properties.get("point"));
      VERSION_BUILD = properties.get("build") != null ? Integer.parseInt(properties.get("build")) : -1;
      VERSION_HASH = properties.get("hash") != null ? properties.get("hash") : "";

      if (VERSION_BUILD == -1) {
        VERSION_FULL = VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_POINT;
        VERSION_MAJOR_MINOR_POINT = VERSION_FULL;
        VERSION = "Development [" + VERSION_FULL + "]";
        IS_DEBUG = true;
      } else {
        VERSION_FULL = VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_POINT + "." + VERSION_BUILD;
        VERSION_MAJOR_MINOR_POINT = VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_POINT;
        VERSION = VERSION_FULL;
        IS_DEBUG = false;
      }

      DEBUG = NAME + " " + VERSION + " for " + PLATFORM;
    } catch (Exception e) {
      e.printStackTrace();
      throw new CubesException(e);
    }
  }
  
  private static HashMap<String, String> map(FileHandle fileHandle) {
    String file = fileHandle.readString();
    String[] split = file.split("\n");
    HashMap<String, String> map = new HashMap<String, String>();
    for (String line: split) {
      line = line.trim();
      if (line.startsWith("#")) continue;
      int index = line.indexOf("=");
      if (index == -1) continue;
      String k = line.substring(0, index).toLowerCase();
      String v = line.substring(index + 1);
      map.put(k, v);
    }
    return map;
  }

  private static FileHandle getFile(String file) {
    FileHandle f = Gdx.files.classpath(file);
    if (f.exists()) return f;
    return Gdx.files.internal(file);
  }
}
