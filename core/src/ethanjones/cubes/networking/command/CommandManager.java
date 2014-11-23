package ethanjones.cubes.networking.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import ethanjones.cubes.core.localization.Localization;

public class CommandManager { //TODO: help text

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
    if (check(commandSender, commandBuilder, arg, new ArrayList<CommandArgument>(), 1)) {
      unknownCommand(commandSender);
    }
  }

  private static boolean check(CommandSender sender, CommandBuilder commandBuilder, ArrayList<String> arg, ArrayList<CommandArgument> arguments, int i) {
    boolean failed = true;
    if (i < arg.size()) {
      for (CommandBuilder builder : commandBuilder.getChildren()) {
        String a = arg.get(i);
        if (builder.getCommandString() != null) {
          if (builder.getCommandString().equals(a)) {
            CommandArgument<String> commandArgument = new CommandArgument<String>(a, null);
            ArrayList<CommandArgument> c = new ArrayList<CommandArgument>();
            c.addAll(arguments);
            c.add(commandArgument);
            failed = check(sender, builder, arg, c, i + 1) ? true : failed;
          }
        } else {
          try {
            Object o = builder.getCommandValue().getArgument(a);
            CommandArgument commandArgument = new CommandArgument(o, builder.getCommandValue());
            ArrayList<CommandArgument> c = new ArrayList<CommandArgument>();
            c.addAll(arguments);
            c.add(commandArgument);
            failed = check(sender, builder, arg, c, i + 1) ? true : failed;
          } catch (Exception e) {

          }
        }
      }
    }
    if (failed) {
      if (commandBuilder.getCommandListener() != null) {
        CommandListener commandListener = commandBuilder.getCommandListener();
        failed = false;
        commandListener.onCommand(commandBuilder, arguments, sender);
      }
    }
    return failed;
  }

  private static void unknownCommand(CommandSender commandSender) {
    commandSender.print(Localization.get("commands.unknown"));
  }

  public static void reset() {
    commands.clear();
  }
}
