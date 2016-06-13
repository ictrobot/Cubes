package ethanjones.cubes.side.server.command;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.side.common.Cubes;

import java.util.List;

public class SaveCommand {

  public static void init() {
    CommandBuilder save = new CommandBuilder("save").register().setCommandPermission(CommandPermission.Extended);

    save.setCommandListener(new CommandListener() {
      @Override
      public void onCommand(CommandBuilder builder, List<CommandArgument> arguments, CommandSender sender) {
        sender.print(Localization.get("Saving..."));
        Cubes.getServer().world.save(null);
      }
    });

    save.add(CommandValue.stringValue).setCommandListener(new CommandListener() {
      @Override
      public void onCommand(CommandBuilder builder, List<CommandArgument> arguments, CommandSender sender) {
        sender.print(Localization.get("Saving..."));
        Cubes.getServer().world.save(((String) arguments.get(1).get()));
      }
    });
  }
}
