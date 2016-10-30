package ethanjones.cubes.core.platform;

import ethanjones.cubes.core.platform.CmdLineParser.IllegalOptionValueException;
import ethanjones.cubes.core.platform.CmdLineParser.Option;
import ethanjones.cubes.core.platform.CmdLineParser.OptionException;
import ethanjones.cubes.world.save.Gamemode;

import java.util.Locale;

public class ServerCmdLineOptions {
  public Long worldSeed;
  public String worldGenerator;
  public Gamemode worldGamemode;
  public Integer port;

  public void parse() {
    CmdLineParser clp = new CmdLineParser(true);
    clp.setHelpStartText("Cubes\n\nWorld options only used when generating new world");
    clp.addIntegerOption('p', "port", "Networking port");
    clp.addOption(new LongStringOption('s', "seed", "World Generator Seed"));
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
    worldSeed = clp.getOptionValue("seed", null);
    worldGenerator = clp.getOptionValue("generator", null);
    String gamemode = clp.getOptionValue("gamemode", null);
    if (gamemode != null) {
      worldGamemode = Gamemode.valueOf(gamemode);
    }
  }

  public static class LongStringOption extends Option<Long> {
    public LongStringOption(char shortForm, String longForm, String helpText) {
      super(shortForm, longForm, true, helpText, "long,string");
    }

    public LongStringOption(String longForm, String helpText) {
      super(longForm, true, helpText, "long,string");
    }

    @Override
    protected Long parseValue(String arg, Locale locale) throws IllegalOptionValueException {
      try {
        return Long.valueOf(arg);
      } catch (NumberFormatException e) {
        return (long) arg.hashCode();
      }
    }
  }
}
