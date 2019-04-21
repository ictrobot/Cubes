package ethanjones.cubes.side.server.commands;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.side.server.command.*;

import java.util.List;

public class NoClipCommand {

  public static void init() {
    CommandBuilder noclip = new CommandBuilder("noclip").register().setCommandPermission(CommandPermission.Extended);

    noclip.add("enable").setCommandListener(new CommandListener() {
      @Override
      public void onCommand(CommandBuilder builder, List<CommandArgument> arguments, CommandSender sender) {
        if (sender instanceof Player) {
          ((Player) sender).setNoClip(true);
          sender.print(Localization.get("command.noclip.enable"));
        } else {
          sender.print(Localization.get("command.common.onlyPlayer"));
        }
      }
    });

    noclip.add("disable").setCommandListener(new CommandListener() {
      @Override
      public void onCommand(CommandBuilder builder, List<CommandArgument> arguments, CommandSender sender) {
        if (sender instanceof Player) {
          ((Player) sender).setNoClip(false);
          sender.print(Localization.get("command.noclip.disable"));
        } else {
          sender.print(Localization.get("command.common.onlyPlayer"));
        }
      }
    });
  }

}
