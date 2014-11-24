package ethanjones.cubes.side.server.command;

import java.util.List;
import java.util.Map.Entry;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.side.common.Cubes;

public class BasicCommands {

  protected static void init() {
    new CommandBuilder("help").register().setCommandListener(new CommandListener() {
      @Override
      public void onCommand(CommandBuilder builder, List<CommandArgument> arguments, CommandSender sender) {
        sender.print(Localization.get("commands.help.commands"));
        for (Entry<String, CommandBuilder> entry : CommandManager.commands.entrySet()) {
          if (!CommandPermission.check(entry.getValue().getCommandPermission(), sender.getPermissionLevel())) continue;
          String str = entry.getKey();
          if (entry.getValue().getHelpText() != null) {
            str = Localization.get("commands.help.format", str, entry.getValue().getHelpText());
          }
          sender.print(str);
        }
      }
    }).add(CommandValue.command).setCommandListener(new CommandListener() {
      @Override
      public void onCommand(CommandBuilder builder, List<CommandArgument> arguments, CommandSender sender) {
        CommandBuilder command = (CommandBuilder) arguments.get(1).get();
        if (!CommandPermission.check(command.getCommandPermission(), sender.getPermissionLevel())) {
          CommandManager.unknownCommand(sender);
          return;
        }
        String base = command.getCommandString();
        sender.print(Localization.get("commands.help.command", base));
        if (command.getHelpText() != null) {
          sender.print(format(base, command.getHelpText()));
        } else {
          sender.print(base);
        }
        for (CommandBuilder child : command.getChildren()) {
          print(child, sender, base);
        }
      }

      private void print(CommandBuilder builder, CommandSender commandSender, String base) {
        String extended;
        if (builder.getCommandString() != null) {
          extended = base + " " + builder.getCommandString();
        } else {
          extended = base + " " + builder.getCommandValue().toString();
        }
        if (CommandPermission.check(builder.getCommandPermission(), commandSender.getPermissionLevel())) {
          if (builder.getHelpText() != null) {
            commandSender.print(format(extended, builder.getHelpText()));
          } else {
            commandSender.print(extended);
          }
        }
        for (CommandBuilder commandBuilder : builder.getChildren()) {
          print(commandBuilder, commandSender, base);
        }
      }

      private String format(String first, String second) {
        return Localization.get(Localization.get("commands.help.format", first, second));
      }
    });

    new CommandBuilder("stop").register().setCommandPermission(CommandPermission.All).setCommandListener(new CommandListener() {
      @Override
      public void onCommand(CommandBuilder builder, List<CommandArgument> arguments, CommandSender sender) {
        sender.print(Localization.get("Stopping..."));
        Cubes.getServer().dispose();
      }
    });
  }
  
}
