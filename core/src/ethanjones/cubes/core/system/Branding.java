package ethanjones.cubes.core.system;

import com.badlogic.gdx.Gdx;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import ethanjones.cubes.core.logging.Log;

public class Branding {

  public final static String NAME;
  public final static String PLATFORM;

  public final static String VERSION_HASH;
  public final static String VERSION_MAJOR;
  public final static String VERSION_MINOR;
  public final static String VERSION_POINT;
  public final static String VERSION_BUILD;
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
        input = Gdx.files.internal("version").read();
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

      if (Gdx.files.internal("build").exists()) {
        try {
          Properties buildProperties = new Properties();
          input = Gdx.files.internal("build").read();
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

      VERSION_MAJOR = properties.getProperty("major");
      VERSION_MINOR = properties.getProperty("minor");
      VERSION_POINT = properties.getProperty("point");
      VERSION_BUILD = properties.getProperty("build") != null ? properties.getProperty("build") : "";
      VERSION_HASH = properties.getProperty("hash") != null ? properties.getProperty("hash") : "";
      VERSION_FULL = VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_POINT + "." + VERSION_BUILD;
      VERSION_MAJOR_MINOR_POINT = VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_POINT;

      if (VERSION_BUILD.isEmpty()) {
        VERSION = "Development [" + VERSION_MAJOR_MINOR_POINT + "]";
        IS_DEBUG = true;
      } else {
        VERSION = VERSION_FULL;
        IS_DEBUG = false;
      }

      DEBUG = NAME + " " + VERSION + " for " + PLATFORM;
    } catch (Exception e) {
      e.printStackTrace();
      throw new CubesException(e);
    }
  }
}
