package ethanjones.modularworld.core.mod;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.side.common.ModularWorld;

import java.io.*;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ModManager {

  private static Array<Properties> propertiesArray = new Array<Properties>();

  private static FileHandle[] getModFiles() {
    FileHandle base = ModularWorld.compatibility.getBaseFolder().child("mods");
    base.mkdirs();
    return base.list(new FileFilter() {
      @Override
      public boolean accept(File pathname) {
        String s = pathname.getName().toLowerCase();
        return s.endsWith(".mod");
      }
    });
  }

  public static void init() {
    ModLoader modLoader = ModularWorld.compatibility.getModLoader();
    FileHandle temp = ModularWorld.compatibility.getBaseFolder().child("modTemp");
    temp.deleteDirectory();
    temp.mkdirs();
    FileHandle modAssets = ModularWorld.compatibility.getBaseFolder().child("modAssets");
    modAssets.deleteDirectory();
    modAssets.mkdirs();
    for (FileHandle fileHandle : getModFiles()) {
      try {
        InputStream inputStream = new FileInputStream(fileHandle.file());
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
          FileHandle f = temp.child(fileHandle.name()).child(entry.getName());
          if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(".jar") && modLoader.supports(ModLoader.Type.jar)) {
            writeToFile(f, zipInputStream);
            modLoader.load(f);
          } else if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(".dex") && ModularWorld.compatibility.getModLoader().supports(ModLoader.Type.dex)) {
            writeToFile(f, zipInputStream);
            modLoader.load(f);
          } else if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(".properties")) {
            Properties properties = new Properties();
            properties.load(zipInputStream);
            propertiesArray.add(properties);
          } else if (entry.getName().startsWith("assets")) {
            writeToFile(modAssets.child(entry.getName()), zipInputStream);
          }
        }
      } catch (Exception e) {
        Log.error("Failed to load mod: " + fileHandle.name(), e);
      }
    }
    for (Properties properties : propertiesArray) {
      if (properties.containsKey("modClass")) {
        try {
          Class<? extends Mod> c = modLoader.loadClass(properties).asSubclass(Mod.class);
          Mod mod = c.newInstance();
          mod.create();
        } catch (Exception e) {
          Log.debug("Failed to make instance of mod: " + properties.getProperty("modClass"), e);
        }
      }
    }
  }

  public static void writeToFile(FileHandle file, ZipInputStream zipInputStream) throws Exception {
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
}
