package ethanjones.cubes.core.lua;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.lua.convert.LuaConversion;
import ethanjones.cubes.core.lua.generation.LuaGeneration;
import ethanjones.cubes.core.lua.java.LuaClass;
import ethanjones.cubes.core.lua.java.LuaPackage;
import ethanjones.cubes.core.mod.ModInstance;
import ethanjones.cubes.core.mod.ModManager;
import ethanjones.cubes.core.mod.lua.LuaMappingMod;
import ethanjones.cubes.core.mod.lua.LuaModInstance;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.system.Debug;

import org.luaj.vm2.*;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.*;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;
import org.luaj.vm2.lib.jse.JseOsLib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import static ethanjones.cubes.core.lua.LuaMapping.mapping;

public class CubesLua {

  public static Globals globals() {
    final Globals globals = new Globals();

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
    globals.rawset("mod", mapping(LuaMappingMod.class));
    globals.rawset("vector", LuaVector.create);
    
    globals.rawset("_", new LuaPackage(""));
    globals.rawset("import", new OneArgFunction() {
      @Override
      public LuaValue call(LuaValue arg) {
        if (arg instanceof LuaPackage) return arg.get("class");
        if (arg.isstring()) {
          try {
            Class<?> cla = Class.forName(arg.toString());
            return new LuaClass(cla);
          } catch (ClassNotFoundException e) {
            throw new LuaError("No such class: " + arg.toString());
          }
        }
        argerror("JPackage or String");
        return null;
      }
    });
    globals.rawset("extend", new VarArgFunction() {
      @Override
      public Varargs invoke(Varargs args) {
        if (args.narg() <= 1 || (args.narg() == 2 && args.arg1().isnil())) throw new LuaError("Invalid extend arguments: " + args.toString());
        LuaTable delegations = args.arg(args.narg()).checktable();
        Class extend = args.arg1().isnil() ? Object.class : convertToClass(args.arg1());
        Class[] inherit = new Class[args.narg() - 2];
        for (int i = 0; i < inherit.length; i++) {
          inherit[i] = convertToClass(args.arg(i + 2));
        }
        Class c = LuaGeneration.extendClass(extend, delegations, inherit);
        return LuaConversion.convertToLua(c);
      }
      
      private Class<?> convertToClass(LuaValue l) {
        if (l instanceof LuaPackage) return ((LuaClass) l.get("class")).getJavaClass();
        if (l.isstring()) {
          try {
            return Class.forName(l.toString());
          } catch (ClassNotFoundException e) {
            throw new LuaError("No such class: " + l.toString());
          }
        } else {
          return (Class) LuaConversion.convertToJava(Class.class, l);
        }
      }
    });
    globals.rawset("Log", new LuaClass(Log.class));
    globals.rawset("print", new VarArgFunction() {
      @Override
      public Varargs invoke(Varargs args) {
        LuaValue tostring = globals.get("tostring");
        if (args.narg() == 1) {
          Log.info(tostring.call(args.arg1()).strvalue().tojstring());
        } else {
          StringBuilder out = new StringBuilder();
          for (int i = 1, n = args.narg(); i <= n; i++) {
            if (i > 1) out.append("\t");
            out.append(tostring.call(args.arg(i)).strvalue().tojstring());
          }
          Log.info(out.toString());
        }
        return NIL;
      }
    });

    LoadState.install(globals);
    LuaC.install(globals);

    if (!(LuaString.s_metatable instanceof ReadOnlyLuaTable)) {
      LuaString.s_metatable = new ReadOnlyLuaTable(LuaString.s_metatable);
    }

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
