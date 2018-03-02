package ethanjones.cubes.side.server.command;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.side.server.commands.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CommandManager {

  public static HashMap<String, CommandBuilder> commands = new HashMap<String, CommandBuilder>();

  protected static void register(CommandBuilder commandBuilder) {
    if (!commands.containsKey(commandBuilder.getCommandString())) {
      commands.put(commandBuilder.getCommandString(), commandBuilder);
    }
  }

  public static void run(String command, CommandSender commandSender) {
    if (command == null || commandSender == null) return;
    ArrayList<String> arg = new ArrayList<String>();
    arg.addAll(Arrays.asList(command.split("  *")));
    if (arg.size() == 0 || !commands.containsKey(arg.get(0))) {
      unknownCommand(commandSender);
      return;
    }
    CommandBuilder commandBuilder = commands.get(arg.get(0));
    ArrayList<CommandArgument> commandArguments = new ArrayList<CommandArgument>();
    commandArguments.add(new CommandArgument<CommandBuilder>(commandBuilder, CommandValue.command));
    if (!check(commandSender, commandBuilder, arg, commandArguments, 1, command)) {
      HelpCommand.print(commandBuilder, commandSender);
    }
  }

  private static boolean check(CommandSender sender, CommandBuilder commandBuilder, ArrayList<String> arg, ArrayList<CommandArgument> arguments, int i, String str) {
    boolean success = false;
    if (i < arg.size()) {
      for (CommandBuilder builder : commandBuilder.getChildren()) {
        if (success) break;
        String a = arg.get(i);
        try {
          if (builder.getCommandString() != null) {
            if (builder.getCommandString().equals(a)) {
              CommandArgument<String> commandArgument = new CommandArgument<String>(a, null);
              ArrayList<CommandArgument> c = new ArrayList<CommandArgument>();
              c.addAll(arguments);
              c.add(commandArgument);
              success = check(sender, builder, arg, c, i + 1, str);
            }
          } else {
            Object o = builder.getCommandValue().getArgument(a);
            CommandArgument commandArgument = new CommandArgument(o, builder.getCommandValue());
            ArrayList<CommandArgument> c = new ArrayList<CommandArgument>();
            c.addAll(arguments);
            c.add(commandArgument);
            success = check(sender, builder, arg, c, i + 1, str);
          }
        } catch (Exception e) {
          if (e instanceof CommandParsingException) {
            sender.print(Localization.get(e.getMessage()));
          } else {
            Log.warning(e.getClass().getSimpleName() + " while running command \"" + str + "\"", e);
            sender.print(Localization.get("command.common.exception", e.getClass().getSimpleName()));
          }
          return true;
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
    commandSender.print(Localization.get("command.common.unknownCommand"));
  }

  public static void permissionCheckFailed(CommandSender commandSender) {
    commandSender.print(Localization.get("command.common.failedPermissionCheck"));
  }

  public static void reset() {
    commands.clear();
    init();
  }

  protected static void init() {
    HelpCommand.init();

    SaveCommand.init();
    TeleportCommand.init();
    TimeCommand.init();
    RainCommand.init();

    StopCommand.init();
    ThreadDumpCommand.init();
  }
}
