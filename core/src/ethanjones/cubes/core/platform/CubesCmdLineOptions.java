package ethanjones.cubes.core.platform;

import ethanjones.cubes.core.mod.ModManager;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.world.save.Gamemode;

import com.badlogic.gdx.Gdx;

import java.util.Locale;

public class CubesCmdLineOptions {

  private CmdLineParser.Option<String> modFolderOption;
  private boolean parsed = false;

  protected CmdLineParser build() {
    CmdLineParser cmdLineParser = new CmdLineParser(true);
    modFolderOption = cmdLineParser.addStringOption("modFolder", "Path to directory containing a Cubes mod");
    return cmdLineParser;
  }

  protected void parse(CmdLineParser cmdLineParser) {
    for (String s : cmdLineParser.getOptionValues(modFolderOption)) {
      ModManager.addExtraModFolder(Gdx.files.absolute(s));
    }
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

    @Override
    protected CmdLineParser build() {
      CmdLineParser clp = super.build();
      clp.setHelpStartText("Cubes\n");
      return clp;
    }
  }

  public static class ServerCmdLineOptions extends CubesCmdLineOptions {
    public String worldSeedString;
    public String worldGenerator;
    public Gamemode worldGamemode;
    public Integer port;

    protected CmdLineParser build() {
      CmdLineParser clp = super.build();
      clp.setHelpStartText("Cubes\n\nWorld options only used when generating new world");
      clp.addIntegerOption('p', "port", "Networking port");
      clp.addStringOption('s', "seed", "World Generator Seed");
      clp.addStringOption('g', "generator", "World Generator");
      clp.addStringOption('m', "gamemode", "World Gamemode");
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
    }
  }
}
