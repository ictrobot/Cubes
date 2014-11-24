package ethanjones.cubes.side.server.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import ethanjones.cubes.core.localization.Localization;

public class CommandManager {

  public static HashMap<String, CommandBuilder> commands = new HashMap<String, CommandBuilder>();

  protected static void register(CommandBuilder commandBuilder) {
    if (!commands.containsKey(commandBuilder.getCommandString())) {
      commands.put(commandBuilder.getCommandString(), commandBuilder);
    }
  }

  public static void run(String command, CommandSender commandSender) {
    if (command == null) return;
    ArrayList<String> arg = new ArrayList<String>();
    arg.addAll(Arrays.asList(command.split("  *")));
    if (arg.size() == 0 || !commands.containsKey(arg.get(0))) {
      unknownCommand(commandSender);
      return;
    }
    CommandBuilder commandBuilder = commands.get(arg.get(0));
    ArrayList<CommandArgument> commandArguments = new ArrayList<CommandArgument>();
    commandArguments.add(new CommandArgument<CommandBuilder>(commandBuilder, CommandValue.command));
    if (!check(commandSender, commandBuilder, arg, commandArguments, 1)) {
      unknownCommand(commandSender);
    }
  }

  private static boolean check(CommandSender sender, CommandBuilder commandBuilder, ArrayList<String> arg, ArrayList<CommandArgument> arguments, int i) {
    boolean success = false;
    if (i < arg.size()) {
      for (CommandBuilder builder : commandBuilder.getChildren()) {
        if (success) break;
        String a = arg.get(i);
        if (builder.getCommandString() != null) {
          if (builder.getCommandString().equals(a)) {
            CommandArgument<String> commandArgument = new CommandArgument<String>(a, null);
            ArrayList<CommandArgument> c = new ArrayList<CommandArgument>();
            c.addAll(arguments);
            c.add(commandArgument);
            success = check(sender, builder, arg, c, i + 1);
          }
        } else {
          try {
            Object o = builder.getCommandValue().getArgument(a);
            CommandArgument commandArgument = new CommandArgument(o, builder.getCommandValue());
            ArrayList<CommandArgument> c = new ArrayList<CommandArgument>();
            c.addAll(arguments);
            c.add(commandArgument);
            success = check(sender, builder, arg, c, i + 1);
          } catch (Exception e) {

          }
        }
      }
    } else {
      if (commandBuilder.getCommandListener() != null) {
        success = true;
        if (CommandPermission.check(commandBuilder.getCommandPermission(), sender.getPermissionLevel())) {
          CommandListener commandListener = commandBuilder.getCommandListener();
          commandListener.onCommand(commandBuilder, arguments, sender);
        } else {
          permissionCheckFailed(sender);
        }
      }
    }
    return success;
  }

  public static void unknownCommand(CommandSender commandSender) {
    commandSender.print(Localization.get("commands.common.unknownCommand"));
  }

  public static void permissionCheckFailed(CommandSender commandSender) {
    commandSender.print(Localization.get("commands.common.failedPermissionCheck"));
  }

  public static void reset() {
    commands.clear();
    BasicCommands.init();
  }
}
