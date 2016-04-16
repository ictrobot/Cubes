package ethanjones.cubes.side.server.command;

public class BasicCommands {

  protected static void init() {
    HelpCommand.init();
    StopCommand.init();
    TeleportCommand.init();
    SpeedCommand.init();
    TimeCommand.init();
  }
  
}
