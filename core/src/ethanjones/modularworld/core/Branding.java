package ethanjones.modularworld.core;

import com.badlogic.gdx.Gdx;
import ethanjones.modularworld.core.debug.Debug;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Branding {

  public final static String NAME;
  public final static String PLATFORM;

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
    NAME = "ModularWorld";
    PLATFORM = Gdx.app.getType().name();

    Properties prop = new Properties();
    InputStream input = null;
    try {
      input = Gdx.files.internal("version").read();
      prop.load(input);
    } catch (IOException ex) {
      throw new ModularWorldException("Failed to load version", ex);
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException e) {

        }
      }
    }
    VERSION_MAJOR = prop.getProperty("major");
    VERSION_MINOR = prop.getProperty("minor");
    VERSION_POINT = prop.getProperty("point");
    VERSION_BUILD = prop.getProperty("build");
    VERSION_FULL = VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_POINT + "." + VERSION_BUILD;
    VERSION_MAJOR_MINOR_POINT = VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_POINT;

    if (VERSION_BUILD.contentEquals("%BUILD_" + "NUMBER%")) {
      VERSION = "Development [" + VERSION_MAJOR_MINOR_POINT + "]";
      IS_DEBUG = true;
    } else {
      VERSION = VERSION_FULL;
      IS_DEBUG = false;
    }

    DEBUG = NAME + " " + VERSION + " for " + PLATFORM;

    Debug.version(Branding.DEBUG);

  }
}
