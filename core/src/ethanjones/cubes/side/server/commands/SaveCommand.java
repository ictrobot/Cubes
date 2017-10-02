package ethanjones.cubes.side.server.commands;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.server.command.*;
import ethanjones.cubes.world.World;

import java.util.List;

public class SaveCommand {

  public static void init() {
    CommandBuilder save = new CommandBuilder("save").register().setCommandPermission(CommandPermission.Extended);

    save.setCommandListener(new CommandListener() {
      @Override
      public void onCommand(CommandBuilder builder, List<CommandArgument> arguments, CommandSender sender) {
        World world = Cubes.getServer().world;
        if (world.save.readOnly) {
          sender.print(Localization.get("command.save.readOnly"));
        } else {
          sender.print(Localization.get( "command.save.starting"));
          Cubes.getServer().world.save();
        }
      }
    });
  }
}
