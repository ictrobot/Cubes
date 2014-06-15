package ethanjones.modularworld;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import com.badlogic.gdx.Gdx;
import ethanjones.modularworld.core.exception.LoadingException;

public class Branding {
  
  public final static String NAME;
  public final static String PLATFORM;
  
  public final static String VERSION_MAJOR;
  public final static String VERSION_MINOR;
  public final static String VERSION_POINT;
  public final static String VERSION_BUILD;
  public final static String VERSION_FULL;
  public final static String VERSION;
  
  public final static String DEBUG;
  
  static {
    NAME = "ModularWorld";
    PLATFORM = Gdx.app.getType().name();
    
    Properties prop = new Properties();
    InputStream input = null;
    try {
      input = Gdx.files.classpath("version").read();
      prop.load(input);
    } catch (IOException ex) {
      throw new LoadingException("Failed to load version", ex);
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
    
    if (VERSION_BUILD.contentEquals("%BUILD_" + "NUMBER%")) {
      VERSION = "Development";
      DEBUG = NAME + " " + VERSION + "[" + VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_POINT + "]" + " for " + PLATFORM;
    } else {
      VERSION = VERSION_FULL;
      DEBUG = NAME + " " + VERSION + " for " + PLATFORM;
    }
  }
}
