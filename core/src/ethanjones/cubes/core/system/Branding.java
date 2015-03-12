package ethanjones.cubes.core.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import ethanjones.cubes.core.logging.Log;

public class Branding {

  public final static String NAME;
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
      NAME = "Cubes";
      PLATFORM = Gdx.app.getType().name();

      Properties properties = new Properties();
      InputStream input = null;
      try {
        input = getFile("version").read();
        properties.load(input);
      } catch (IOException ex) {
        Log.error("Failed to load version", ex);
      } finally {
        if (input != null) {
          try {
            input.close();
          } catch (IOException e) {

          }
        }
      }

      FileHandle buildFile = getFile("build");
      if (buildFile.exists()) {
        try {
          Properties buildProperties = new Properties();
          input = buildFile.read();
          buildProperties.load(input);
          if (buildProperties.getProperty("build") != null) {
            properties.setProperty("build", buildProperties.getProperty("build"));
            properties.setProperty("hash", buildProperties.getProperty("hash"));
          }
        } catch (IOException ex) {
          Log.error("Failed to load build", ex);
        } finally {
          if (input != null) {
            try {
              input.close();
            } catch (IOException e) {

            }
          }
        }
      }

      VERSION_MAJOR = Integer.parseInt(properties.getProperty("major"));
      VERSION_MINOR = Integer.parseInt(properties.getProperty("minor"));
      VERSION_POINT = Integer.parseInt(properties.getProperty("point"));
      VERSION_BUILD = properties.getProperty("build") != null ? Integer.parseInt(properties.getProperty("build")) : -1;
      VERSION_HASH = properties.getProperty("hash") != null ? properties.getProperty("hash") : "";

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

  private static FileHandle getFile(String file) {
    FileHandle f = Gdx.files.classpath(file);
    if (f.exists()) return f;
    return Gdx.files.internal(file);
  }
}
