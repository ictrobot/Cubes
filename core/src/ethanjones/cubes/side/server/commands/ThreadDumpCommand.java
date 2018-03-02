package ethanjones.cubes.side.server.commands;

import ethanjones.cubes.side.server.command.*;

import java.util.List;
import java.util.Map;

public class ThreadDumpCommand {

  public static void init() {
    new CommandBuilder("threads").register().setCommandPermission(CommandPermission.All).setCommandListener(new CommandListener() {
      @Override
      public void onCommand(CommandBuilder builder, List<CommandArgument> arguments, CommandSender sender) {
        for (Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
          sender.print(entry.getKey().getName() + " - " + entry.getKey().getId());
          for (StackTraceElement stackTraceElement : entry.getValue()) {
            sender.print(" - " + stackTraceElement.toString());
          }
        }
      }
    });
  }
}
