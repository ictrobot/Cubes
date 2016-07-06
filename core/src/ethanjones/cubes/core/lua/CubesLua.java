package ethanjones.cubes.core.lua;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.mod.ModInstance;
import ethanjones.cubes.core.mod.ModManager;
import ethanjones.cubes.core.mod.lua.LuaMappingMod;
import ethanjones.cubes.core.mod.lua.LuaModInstance;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.system.Debug;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.*;
import org.luaj.vm2.lib.jse.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import static ethanjones.cubes.core.lua.LuaMapping.mapping;

public class CubesLua {

  public static Globals globals() {
    Globals globals = new Globals();

    // modified default libraries
    globals.load(new CubesBaseLib());     // redefine findResource
    globals.load(new CubesPackageLib());  // remove java_searcher
    globals.load(new Bit32Lib());
    globals.load(new TableLib());
    globals.load(new StringLib());
    globals.load(new CoroutineLib());
    globals.load(new JseMathLib());
    //globals.load(new JseIoLib());
    globals.load(new CubesOSLib());       // limit functionality
    //globals.load(new LuajavaLib());

    // cubes libraries
    globals.rawset("cubes", mapping(LuaMappingCubes.class));
    globals.rawset("log", mapping(LuaMappingLog.class));
    globals.rawset("mod", mapping(LuaMappingMod.class));
    globals.rawset("vector", LuaVector.create);

    LoadState.install(globals);
    LuaC.install(globals);
    return globals;
  }


  private static class CubesBaseLib extends JseBaseLib {
    private static final String regex = Pattern.quote("|");

    @Override
    public InputStream findResource(String filename) {
      String[] parts = filename.split(regex);
      if (parts.length != 2) return null;
      String mod = parts[0];
      LuaModInstance m = null;
      for (ModInstance instance : ModManager.getMods()) {
        if (instance instanceof LuaModInstance && instance.getName().equals(mod)) {
          m = (LuaModInstance) instance;
          break;
        }
      }
      if (m == null) return null;
      File modRoot = m.luaFolder.file();
      File file = new File(modRoot, parts[1]);
      try {
        String canonicalModRoot = modRoot.getCanonicalPath();
        String canonicalFile = file.getCanonicalPath();
        if (!canonicalFile.startsWith(canonicalModRoot)) {
          Log.warning("Attempt to access '" + canonicalFile + "' by " + String.valueOf(ModManager.getCurrentMod()));
          return null;
        }
      } catch (IOException e) {
        Debug.crash(new CubesException("Failed to check file was within mod lua root", e));
      }
      if (file.exists() && !file.isDirectory()) {
        try {
          return new FileInputStream(file);
        } catch (IOException ioe) {
          return null;
        }
      }
      return null;
    }

  }

  private static class CubesPackageLib extends PackageLib {

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
      env = super.call(modname, env);
      try {
        LuaTable _package = (LuaTable) env.get("package");
        LuaTable searchers = (LuaTable) _package.get("searchers");
        if (searchers.remove(3) != java_searcher) throw new Exception("Failed to remove java_searcher");
        java_searcher = null;
      } catch (Exception e) {
        Debug.crash(new CubesException("Failed to remove java_searcher", e));
      }
      return env;
    }

  }

  private static class CubesOSLib extends JseOsLib {

    private static final String[] ALLOWED = {
            "clock",
            "date",
            "difftime",
            "time"
    };

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
      LuaValue v = super.call(modname, env);
      if (v != env.get("os")) throw new IllegalStateException();
      LuaTable original = ((LuaTable) v);
      LuaTable os = new LuaTable();
      for (String s : ALLOWED) {
        os.set(s, original.get(s));
      }
      env.set("os", os);
      if (os != env.get("os")) throw new IllegalStateException();
      env.get("package").get("loaded").set("os", os);
      return os;
    }

  }
}
