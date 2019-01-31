package ethanjones.cubes.core.platform;

import ethanjones.cubes.core.mod.ModManager;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.world.save.Gamemode;

import com.badlogic.gdx.Gdx;

import java.util.Locale;

public class CubesCmdLineOptions {

  private CmdLineParser.Option<String> modFileOption;
  private boolean parsed = false;

  protected CmdLineParser build() {
    CmdLineParser cmdLineParser = new CmdLineParser(true);
    modFileOption = cmdLineParser.addStringOption("mod", "Path to a Cubes mod");
    Compatibility.get().setupCmdLineOptions(cmdLineParser);
    return cmdLineParser;
  }

  protected void parse(CmdLineParser cmdLineParser) {
    for (String s : cmdLineParser.getOptionValues(modFileOption)) {
      ModManager.addExtraMod(Gdx.files.absolute(s));
    }
    Compatibility.get().parseCmdLineOptions(cmdLineParser);
  }

  public final void parse() {
    if (parsed) return;
    try {
      CmdLineParser cmdLineParser = build();
      try {
        cmdLineParser.parse(Compatibility.get().getCommandLineArgs(), Locale.UK);
      } catch (CmdLineParser.OptionException e) {
        e.printStackTrace();
        Adapter.quit();
      }
      if (cmdLineParser.helpCalled()) {
        Adapter.quit();
      }
      parse(cmdLineParser);
    } catch (Exception e) {
      Debug.crash(new CubesException("Failed to parse command line options", e));
    }
    parsed = true;
  }

  public static class ClientCmdLineOptions extends CubesCmdLineOptions {
    public boolean loadTemporaryWorld;

    @Override
    protected CmdLineParser build() {
      CmdLineParser clp = super.build();
      clp.setHelpStartText("Cubes\n");
      clp.addBooleanOption("loadTemporaryWorld", "Causes Cubes to generate a new temporary single player world immediately instead of loading to main menu");
      return clp;
    }

    @Override
    protected void parse(CmdLineParser cmdLineParser) {
      Boolean b = cmdLineParser.getOptionValue("loadTemporaryWorld", false);
      loadTemporaryWorld = b != null ? b : false;
    }
  }

  public static class ServerCmdLineOptions extends CubesCmdLineOptions {
    public String worldSeedString;
    public String worldGenerator;
    public Gamemode worldGamemode;
    public Integer port;
    public String worldFolder;

    protected CmdLineParser build() {
      CmdLineParser clp = super.build();
      clp.setHelpStartText("Cubes\n\nWorld options only used when generating new world");
      clp.addIntegerOption('p', "port", "Networking port");
      clp.addStringOption("seed", "World Generator Seed");
      clp.addStringOption("generator", "World Generator");
      clp.addStringOption("gamemode", "World Gamemode");
      clp.addStringOption("world-folder", "World Folder");
      return clp;
    }

    protected void parse(CmdLineParser clp) {
      super.parse(clp);
      port = clp.getOptionValue("port", null);
      worldSeedString = clp.getOptionValue("seed", null);
      worldGenerator = clp.getOptionValue("generator", null);
      if (worldGenerator != null && !worldGenerator.contains(":")) {
        worldGenerator = "core:" + worldGenerator;
      }
      String gamemode = clp.getOptionValue("gamemode", null);
      if (gamemode != null) {
        worldGamemode = Gamemode.valueOf(gamemode);
      }
      worldFolder = clp.getOptionValue("world-folder", null);
    }
  }
}
