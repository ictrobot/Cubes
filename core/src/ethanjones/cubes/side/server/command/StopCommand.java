package ethanjones.cubes.side.server.command;

import java.util.List;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.side.common.Cubes;

public class StopCommand {

  public static void init() {
    new CommandBuilder("stop").register().setCommandPermission(CommandPermission.All).setCommandListener(new CommandListener() {
      @Override
      public void onCommand(CommandBuilder builder, List<CommandArgument> arguments, CommandSender sender) {
        sender.print(Localization.get("Stopping..."));
        Cubes.getServer().dispose();
      }
    });
  }
}
