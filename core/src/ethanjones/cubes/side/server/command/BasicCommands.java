package ethanjones.cubes.side.server.command;

public class BasicCommands {

  protected static void init() {
    HelpCommand.init();
    SaveCommand.init();
    SpeedCommand.init();
    StopCommand.init();
    TeleportCommand.init();
    TimeCommand.init();
  }
  
}
