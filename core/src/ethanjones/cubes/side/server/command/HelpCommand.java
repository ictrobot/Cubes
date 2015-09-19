package ethanjones.cubes.side.server.command;

import ethanjones.cubes.core.localization.Localization;

import java.util.List;
import java.util.Map.Entry;

public class HelpCommand {

  public static void init() {

    new CommandBuilder("help").register().setCommandListener(new CommandListener() {
      @Override
      public void onCommand(CommandBuilder builder, List<CommandArgument> arguments, CommandSender sender) {
        sender.print(Localization.get("command.help.commands"));
        for (Entry<String, CommandBuilder> entry : CommandManager.commands.entrySet()) {
          if (!CommandPermission.check(entry.getValue().getCommandPermission(), sender.getPermissionLevel())) continue;
          String str = entry.getKey();
          if (entry.getValue().getHelpText() != null) {
            str = Localization.get("command.help.format", str, entry.getValue().getHelpText());
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
        print(command, sender);
      }
    });
  }

  public static void print(CommandBuilder builder, CommandSender sender) {
    String base = builder.getCommandString();
    sender.print(Localization.get("command.help.command", base));
    if (builder.getCommandListener() != null) {
      if (builder.getHelpText() != null) {
        sender.print(format(base, builder.getHelpText()));
      } else {
        sender.print(base);
      }
    }
    for (CommandBuilder child : builder.getChildren()) {
      print(child, sender, base);
    }
  }

  private static void print(CommandBuilder builder, CommandSender sender, String base) {
    String extended;
    if (builder.getCommandString() != null) {
      extended = base + " " + builder.getCommandString();
    } else {
      extended = base + " " + builder.getCommandValue().toString();
    }
    if (builder.getCommandListener() != null && CommandPermission.check(builder.getCommandPermission(), sender.getPermissionLevel())) {
      if (builder.getHelpText() != null) {
        sender.print(format(extended, builder.getHelpText()));
      } else {
        sender.print(extended);
      }
    }
    for (CommandBuilder commandBuilder : builder.getChildren()) {
      print(commandBuilder, sender, extended);
    }
  }

  private static String format(String first, String second) {
    return Localization.get(Localization.get("command.help.format", first, second));
  }
  
}
