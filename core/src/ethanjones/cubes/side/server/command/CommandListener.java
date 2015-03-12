package ethanjones.cubes.side.server.command;

import java.util.List;

public interface CommandListener {

  public void onCommand(CommandBuilder builder, List<CommandArgument> arguments, CommandSender sender);
  
}
