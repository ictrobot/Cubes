package ethanjones.cubes.core.platform.desktop;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.graphics.assets.Asset;
import ethanjones.cubes.graphics.assets.AssetFinder;
import ethanjones.cubes.graphics.assets.AssetManager;
import ethanjones.cubes.graphics.assets.Assets;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static ethanjones.cubes.graphics.assets.AssetFinder.addAssetManager;

public abstract class DesktopCompatibility extends Compatibility {

  protected static void setup() {
    DesktopSecurityManager.setup();
    DesktopMemoryChecker.setup();
  }

  public final OS os;
  private final String[] arg;

  protected DesktopCompatibility(DesktopLauncher desktopLauncher, Application.ApplicationType applicationType, String[] arg) {
    super(desktopLauncher, applicationType);
    this.arg = arg;

    String str = (System.getProperty("os.name")).toUpperCase();
    if (str.contains("WIN")) {
      os = OS.Windows;
    } else if (str.contains("MAC")) {
      os = OS.Mac;
    } else if (str.contains("LINUX")) {
      os = OS.Linux;
    } else {
      os = OS.Unknown;
    }
  }

  @Override
  public void setupAssets() {
    if (Gdx.files.classpath("version").exists()) { //files in a jar
      Log.debug("Extracting assets");
      extractCoreAssets();
    } else {
      Log.debug("Finding assets");
      super.setupAssets();
    }
  }

  @Override
  public void logEnvironment() {
    Log.debug("Java Home:          " + System.getProperty("java.home"));
    Log.debug("Java Vendor:        " + System.getProperty("java.vendor"));
    Log.debug("Java Vendor URL:    " + System.getProperty("java.vendor.url"));
    Log.debug("Java Version:       " + System.getProperty("java.version"));
    Log.debug("OS Name:            " + System.getProperty("os.name"));
    Log.debug("OS Architecture:    " + System.getProperty("os.arch"));
    Log.debug("OS Version:         " + System.getProperty("os.version"));
    Log.debug("User Home:          " + System.getProperty("user.home"));
    Log.debug("Working Directory:  " + System.getProperty("user.dir"));
    Runtime runtime = Runtime.getRuntime();
    Log.debug("Maximum Memory:     " + (int) (runtime.maxMemory() / 1048576) + "MB");
  }

  @Override
  public boolean isTouchScreen() {
    return false;
  }

  @Override
  public int getFreeMemory() {
    Runtime runtime = Runtime.getRuntime();
    int max = (int) (runtime.maxMemory() / 1048576); //1024 * 1024. Divide to get MB
    int allocated = (int) (runtime.totalMemory() / 1048576);
    int free = (int) (runtime.freeMemory() / 1048576);
    return free + (max - allocated);
  }

  @Override
  public boolean functionModifier() {
    return Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
  }

  @Override
  public String[] getCommandLineArgs() {
    return arg;
  }
  
  @Override
  public void _exit(int status) {
    System.exit(status);
  }
  
  
  private static void extractAssets(URL jar, AssetManager assetManager) {
    String assets = "assets";
    try {
      ZipInputStream zip = new ZipInputStream(getInputStream(jar));
      ZipEntry ze;
      while ((ze = zip.getNextEntry()) != null) {
        String name = ze.getName().replace("\\", "/");
        if (name.startsWith(assets) && !ze.isDirectory()) {
          name = name.substring(ze.getName().lastIndexOf(assets) + assets.length() + 1);
          assetManager.assets.put(name, new Asset(assetManager, name, Gdx.files.internal(ze.getName())));
        }
      }
      zip.close();
    } catch (Exception e) {
      Log.error("Failed to extract assets", e);
    }
  }
  
  public static void extractCoreAssets() {
    AssetManager assetManager = new AssetManager(Assets.CORE);
    URL jar = AssetFinder.class.getProtectionDomain().getCodeSource().getLocation();
    extractAssets(jar, assetManager);
    addAssetManager(assetManager);
  }
  
  private static InputStream getInputStream(URL jar) throws IOException {
    //to fix offset caused by launch4j
    InputStream is = jar.openStream();
    Log.debug("Scanning for jar...");
    long offset = 0;
    boolean found = false;
    try {
      while (true) {
        if (getUnsignedInt(is) == 0x04034b50L) {
          found = true;
          break;
        }
        offset += 4;
      }
    } catch (IOException ignored) {
    }
    is.close();
    
    InputStream finalIS = jar.openStream();
    if (!found) {
      Log.debug("Failed to find start");
    } else {
      Log.debug("Skipping " + offset + " bytes until start of jar [" + finalIS.skip(offset) + "]");
      if (finalIS.markSupported()) finalIS.mark(Integer.MAX_VALUE);
    }
    return finalIS;
  }
  
  private static long getUnsignedInt(InputStream inputStream) throws IOException {
    long a = inputStream.read();
    if (a == -1) throw new IOException();
    long b = inputStream.read();
    if (b == -1) throw new IOException();
    long c = inputStream.read();
    if (c == -1) throw new IOException();
    long d = inputStream.read();
    if (d == -1) throw new IOException();
    
    b <<= 8;
    c <<= 16;
    d <<= 24;
    return a | b | c | d;
  }
  
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy HH:mm:ss");
  
  @Override
  public String timestamp() {
    return dateFormat.format(new Date());
  }
  
  @Override
  public String line_separator() {
    return System.getProperty("line.separator");
  }
}
