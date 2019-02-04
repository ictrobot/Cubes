package ethanjones.cubes.core.system;

import ethanjones.cubes.core.logging.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

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

  public final static boolean IS_RELEASE;
  public final static boolean IS_DEBUG;
  public final static String DEBUG;

  public final static DateFormat DATE_FORMAT;
  public final static DateFormat DISPLAY_DATE_FORMAT;
  public final static Date BUILD_DATE;

  public final static String LAUNCHER;

  static {
    try {
      NAME = "Cubes";
      AUTHOR = "Ethan Jones";
      PLATFORM = Gdx.app.getType().name();

      DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
      DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
      DISPLAY_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      DISPLAY_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));

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
            properties.setProperty("isRelease", buildProperties.getProperty("isRelease"));
            properties.setProperty("buildDate", buildProperties.getProperty("buildDate"));
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
      IS_RELEASE = properties.getProperty("isRelease") != null ? Boolean.valueOf(properties.getProperty("isRelease")) : false;

      if (VERSION_BUILD == -1) {
        VERSION_FULL = VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_POINT;
        VERSION_MAJOR_MINOR_POINT = VERSION_FULL;
        VERSION = "Development [" + VERSION_FULL + "]";
        BUILD_DATE = null;
        IS_DEBUG = true;
      } else {
        VERSION_FULL = VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_POINT + "." + VERSION_BUILD;
        VERSION_MAJOR_MINOR_POINT = VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_POINT;
        if (IS_RELEASE) {
          VERSION = VERSION_FULL;
        } else {
          VERSION = VERSION_FULL + "-dev";
        }
        IS_DEBUG = false;

        BUILD_DATE = DATE_FORMAT.parse(properties.getProperty("buildDate"));
      }

      DEBUG = NAME + " " + VERSION + " for " + PLATFORM;

      LAUNCHER = System.getProperty("ethanjones.cubes.launcher", "");
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
