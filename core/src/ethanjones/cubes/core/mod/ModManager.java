package ethanjones.cubes.core.mod;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.mod.ModInputStream.ModFile;
import ethanjones.cubes.core.mod.ModLoader.ModType;
import ethanjones.cubes.core.mod.event.ModEvent;
import ethanjones.cubes.core.mod.java.JavaModInstance;
import ethanjones.cubes.core.mod.json.JsonModInstance;
import ethanjones.cubes.core.mod.lua.LuaModInstance;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.graphics.assets.AssetFinder;
import ethanjones.cubes.graphics.assets.AssetManager;
import ethanjones.cubes.graphics.assets.Assets;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.StreamUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

public class ModManager {

  private static List<FileHandle> extraMods = new ArrayList<FileHandle>();
  private static boolean init = false;
  private static List<ModInstance> mods;
  private static ModInstance currentMod;

  public synchronized static void addExtraMod(FileHandle fileHandle) {
    if (init) throw new IllegalArgumentException("Must be run before ModManager.init()");
    if (fileHandle == null) throw new IllegalArgumentException("FileHandle cannot be null");
    extraMods.add(fileHandle);
  }

  public synchronized static void init() {
    if (init) return;
    init = true;
    
    Log.debug("Loading mods");
    ArrayList<ModInstance> mods = new ArrayList<ModInstance>();
    ModLoader modLoader = Compatibility.get().getModLoader();
    FileHandle temp = Compatibility.get().getBaseFolder().child("mods").child("temp");
    temp.deleteDirectory();
    temp.mkdirs();
    Compatibility.get().nomedia(temp);
  
    ModType[] types = modLoader.getTypes();
    int loadJar = -1;
    int loadDex = -1;
    for (int i = 0; i < types.length; i++) {
      if (types[i] == ModType.jar) {
        loadJar = i;
      } else if (types[i] == ModType.dex) {
        loadDex = i;
      }
    }
    
    for (FileHandle fileHandle : getModFiles()) {
      if (extraMods.contains(fileHandle)) Log.warning("Loading mod from " + fileHandle.file().getAbsolutePath());
      FileHandle classFile = null;
      String className = null;
      String name = "";
      int selectedPriority = -1;
      ModType selectedModType = null;
      Map<String, FileHandle> jsonFiles = new HashMap<String, FileHandle>();
      Map<String, FileHandle> luaFiles = new HashMap<String, FileHandle>();
      FileHandle modAssets = Assets.assetsFolder.child(fileHandle.name());
      
      ModInputStream mis = null;
      try {
        mis = ModInputStream.get(fileHandle);
        ModFile modFile;
        while ((modFile = mis.getNextModFile()) != null) {
          if (modFile.isFolder()) continue;
          String modFileName = modFile.getName();
          FileHandle f = temp.child(fileHandle.name()).child(modFileName);
          if (modFileName.equals("mod.jar")) {
            if (loadJar != -1 && (selectedPriority == -1 || loadJar < selectedPriority)) {
              writeToFile(f, modFile);
              classFile = f;
              selectedPriority = loadJar;
              selectedModType = ModType.jar;
            }
          } else if (modFileName.equals("mod.dex")) {
            if (loadDex != -1 && (selectedPriority == -1 || loadDex < selectedPriority)) {
              writeToFile(f, modFile);
              classFile = f;
              selectedPriority = loadDex;
              selectedModType = ModType.jar;
            }
          } else if (modFileName.equals("mod.properties")) {
            Properties properties = new Properties();
            InputStream propertiesStream = null;
            try {
              propertiesStream = modFile.getInputStream();
              properties.load(propertiesStream);
            } finally {
              StreamUtils.closeQuietly(propertiesStream);
            }
            className = properties.getProperty("modClass");
            name = properties.getProperty("modName");
          } else if (modFileName.startsWith("assets/")) {
            writeToFile(modAssets.child(modFileName.substring(7)), modFile);
          } else if (modFileName.startsWith("json/") && modFileName.endsWith(".json")) {
            writeToFile(f, modFile);
            jsonFiles.put(modFileName.substring(5), f);
          } else if (modFileName.startsWith("lua/") && modFileName.endsWith(".lua")) {
            writeToFile(f, modFile);
            luaFiles.put(modFileName.substring(4), f);
          }
        }
        if (name == null) {
          Log.error("Mod " + fileHandle.name() + " does not contain a properties file");
          continue;
        }
        if (jsonFiles.isEmpty() && luaFiles.isEmpty()) {
          if (className == null) {
            Log.error("Mod " + fileHandle.name() + " does not contain a \"mod.properties\" with a \"className\" or json or lua files");
            continue;
          }
          if (classFile == null) {
            Log.error("Mod " + fileHandle.name() + " does not contain any of " + Arrays.deepToString(modLoader.getTypes()));
            continue;
          }
        } else {
          if (className == null && classFile != null) {
            Log.error("Mod " + fileHandle.name() + " does not contain a \"mod.properties\" with a \"className\"");
            continue;
          } else if (className != null && classFile == null) {
            Log.error("Mod " + fileHandle.name() + " does not contain any of " + Arrays.deepToString(modLoader.getTypes()));
            continue;
          }
        }
      } catch (Exception e) {
        Log.error("Failed to load mod: " + name, e);
        continue;
      } finally {
        StreamUtils.closeQuietly(mis);
      }
      try {
        Log.debug("Mod file: \"" + fileHandle.name() + "\" Name: \"" + name + "\"");
        AssetManager assetManager;
        if (modAssets.exists() && modAssets.isDirectory()) {
          Log.debug("Loading assets for " + name);
          assetManager = AssetFinder.findAssets(modAssets, name);
        } else {
          Log.debug("No assets detected for " + name);
          assetManager = null;
        }
        jsonFiles = Collections.unmodifiableMap(jsonFiles);
        if (!jsonFiles.isEmpty()) {
          Log.debug("Initialising JsonModInstance");
          JsonModInstance jsonModInstance = new JsonModInstance(name, fileHandle, assetManager, jsonFiles);
          init(jsonModInstance);
          mods.add(jsonModInstance);
          Log.info("Loaded Json mod " + name);
        }
        luaFiles = Collections.unmodifiableMap(luaFiles);
        if (!luaFiles.isEmpty()) {
          Log.debug("Initialising LuaModInstance");
          FileHandle luaFolder = temp.child(fileHandle.name()).child("lua");
          LuaModInstance luaModInstance = new LuaModInstance(name, fileHandle, assetManager, luaFiles, luaFolder);
          init(luaModInstance);
          mods.add(luaModInstance);
          Log.info("Loaded Lua mod " + name);
        }
        if (className != null) {
          Log.info("Trying to load Java mod " + name + " [" + selectedModType + "]");
          Class<?> c = modLoader.loadClass(classFile, className, selectedModType);
          Log.debug("Creating instance of Java mod " + name);
          Object mod = c.newInstance();
          Log.debug("Initialising JavaModInstance");
          JavaModInstance javaModInstance = new JavaModInstance(name, fileHandle, assetManager, mod);
          init(javaModInstance);
          mods.add(javaModInstance);
          Log.info("Loaded Java mod " + name);
        }

      } catch (Exception e) {
        throw new CubesException("Failed to make instance of mod: " + name, e);
      }
    }
    ModManager.mods = Collections.unmodifiableList(mods);
  }

  private static List<FileHandle> getModFiles() {
    FileHandle base = Compatibility.get().getBaseFolder().child("mods");
    base.mkdirs();
    Compatibility.get().nomedia(base);
    ArrayList<FileHandle> fileHandles = new ArrayList<FileHandle>();
    fileHandles.addAll(extraMods);
    Collections.addAll(fileHandles, base.list(new FileFilter() {
      @Override
      public boolean accept(File pathname) {
        String s = pathname.getName().toLowerCase();
        return s.endsWith(".cm");
      }
    }));
    return fileHandles;
  }

  private static void writeToFile(FileHandle file, ModFile modFile) throws Exception {
    InputStream inputStream = null;
    FileOutputStream fileOutputStream = null;
    try {
      inputStream = modFile.getInputStream();
      
      file.parent().mkdirs();
      Compatibility.get().nomedia(file.parent());
      file.file().createNewFile();
      
      fileOutputStream = new FileOutputStream(file.file());
      StreamUtils.copyStream(inputStream, fileOutputStream);
    } catch (Exception e) {
      throw e;
    } finally {
      StreamUtils.closeQuietly(inputStream);
      StreamUtils.closeQuietly(fileOutputStream);
    }
  }

  public synchronized static List<ModInstance> getMods() {
    return mods;
  }

  private synchronized static void init(ModInstance instance) {
    currentMod = instance;
    try {
      instance.init();
    } catch (Exception e) {
      currentMod = null;
      throw new CubesException("Failed to init mod", e);
    }
    currentMod = null;
  }

  public synchronized static void postModEvent(ModEvent modEvent) {
    Log.debug("Posting " + modEvent.getClass().getSimpleName());
    for (ModInstance mod : mods) {
      currentMod = mod;
      try {
        mod.event(modEvent);
      } catch (Exception e) {
        mod.addState(ModState.Error);
        currentMod = null;
        throw new CubesException("Exception while posting " + modEvent.getClass().getSimpleName() + " to mod " + mod.name, e);
      }
    }
    currentMod = null;
    Log.debug("Finished posting " + modEvent.getClass().getSimpleName());
  }

  public synchronized static ModInstance getCurrentMod() {
    return currentMod;
  }

  public synchronized static String getCurrentModName() {
    if (currentMod != null) return currentMod.name;
    return "core";
  }
}
