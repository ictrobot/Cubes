package ethanjones.cubes.server.command;

public class BasicCommands {

  protected static void init() {
    HelpCommand.init();
    StopCommand.init();
    TeleportCommand.init();
  }
  
}
