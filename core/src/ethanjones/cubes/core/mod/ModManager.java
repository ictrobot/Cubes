package ethanjones.cubes.core.mod;

import com.badlogic.gdx.files.FileHandle;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import ethanjones.cubes.core.compatibility.Compatibility;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.mod.ModLoader.ModType;
import ethanjones.cubes.core.mod.event.ModEvent;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.graphics.assets.AssetFinder;
import ethanjones.cubes.graphics.assets.Assets;

public class ModManager {

  private static boolean init = false;
  private static List<ModInstance> mods;

  public static void init() {
    if (init) return;
    init = true;
    ArrayList<ModInstance> mods = new ArrayList<ModInstance>();
    ModLoader modLoader = Compatibility.get().getModLoader();
    FileHandle temp = Compatibility.get().getBaseFolder().child("mods").child("temp");
    temp.deleteDirectory();
    temp.mkdirs();
    for (FileHandle fileHandle : getModFiles()) {
      FileHandle classFile = null;
      String className = null;
      String modName = "";
      FileHandle modAssets = Assets.assetsFolder.child(fileHandle.name());
      try {
        modName = fileHandle.name();
        InputStream inputStream = new FileInputStream(fileHandle.file());
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
          FileHandle f = temp.child(fileHandle.name()).child(entry.getName());
          if (!entry.isDirectory() && entry.getName().toLowerCase().equals("mod.jar")) {
            if (modLoader.supports(ModType.jar)) {
              writeToFile(f, zipInputStream);
              classFile = f;
            }
          } else if (!entry.isDirectory() && entry.getName().toLowerCase().equals("mod.dex")) {
            if (modLoader.supports(ModType.dex)) {
              writeToFile(f, zipInputStream);
              classFile = f;
            }
          } else if (!entry.isDirectory() && entry.getName().toLowerCase().equals("mod.properties")) {
            Properties properties = new Properties();
            properties.load(zipInputStream);
            className = properties.getProperty("modClass");
          } else if (!entry.isDirectory() && entry.getName().toLowerCase().startsWith("assets/")) {
            writeToFile(modAssets.child(entry.getName().substring(7)), zipInputStream);
          }
        }
        if (classFile == null) {
          Log.error("Mod " + modName + " does not contain a jar/dex");
          continue;
        }
        if (className == null) {
          Log.error("Mod " + modName + " does not contain a properties file");
          continue;
        }
      } catch (Exception e) {
        Log.error("Failed to load mod: " + modName, e);
      }
      try {
        if (modAssets.exists() && modAssets.isDirectory()) {
          Log.debug("Loading assets for " + modName);
          AssetFinder.findAssets(modAssets, modName);
        } else {
          Log.debug("No assets detected for " + modName);
        }
        Log.info("Trying to load mod " + modName);
        Class<?> c = modLoader.loadClass(classFile, className);
        Log.debug("Creating instance of mod " + modName);
        Object mod = c.newInstance();
        Log.debug("Initialising ModInstance");
        ModInstance modInstance = new ModInstance(mod, fileHandle);
        modInstance.init();
        mods.add(modInstance);
        Log.info("Loaded mod " + modName);
      } catch (Exception e) {
        Log.debug("Failed to make instance of mod: " + className, e);
      }
    }
    ModManager.mods = Collections.unmodifiableList(mods);
  }

  private static FileHandle[] getModFiles() {
    FileHandle base = Compatibility.get().getBaseFolder().child("mods");
    base.mkdirs();
    return base.list(new FileFilter() {
      @Override
      public boolean accept(File pathname) {
        String s = pathname.getName().toLowerCase();
        return s.endsWith(".cm");
      }
    });
  }

  private static void writeToFile(FileHandle file, ZipInputStream zipInputStream) throws Exception {
    file.parent().mkdirs();
    file.file().createNewFile();
    FileOutputStream fileOutputStream = new FileOutputStream(file.file());
    int len = 0;
    byte[] buffer = new byte[2048];
    while ((len = zipInputStream.read(buffer)) > 0) {
      fileOutputStream.write(buffer, 0, len);
    }
    fileOutputStream.close();
  }

  public static List<ModInstance> getMods() {
    return mods;
  }

  public static void postModEvent(ModEvent modEvent) {
    Log.debug("Posting " + modEvent.getClass().getSimpleName());
    for (ModInstance mod : mods) {
      try {
        mod.event(modEvent);
      } catch (Exception e) {
        throw new CubesException("Exception while posting " + modEvent.getClass().getSimpleName() + " to mod " + mod.getMod().getClass().getSimpleName(), e);
      }
    }
  }
}
