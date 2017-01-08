package ethanjones.cubes.core.platform;

import ethanjones.cubes.core.platform.CmdLineParser.OptionException;
import ethanjones.cubes.world.save.Gamemode;

import java.util.Locale;

public class ServerCmdLineOptions {
  public String worldSeedString;
  public String worldGenerator;
  public Gamemode worldGamemode;
  public Integer port;

  public void parse() {
    CmdLineParser clp = new CmdLineParser(true);
    clp.setHelpStartText("Cubes\n\nWorld options only used when generating new world");
    clp.addIntegerOption('p', "port", "Networking port");
    clp.addStringOption('s', "seed", "World Generator Seed");
    clp.addStringOption('g', "generator", "World Generator");
    clp.addStringOption('m', "gamemode", "World Gamemode");
    try {
      clp.parse(Compatibility.get().getCommandLineArgs(), Locale.UK);
    } catch (OptionException e) {
      e.printStackTrace();
      Adapter.quit();
    }
    if (clp.helpCalled()) {
      Adapter.quit();
    }
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
