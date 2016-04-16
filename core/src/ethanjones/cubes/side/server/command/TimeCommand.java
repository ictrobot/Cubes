package ethanjones.cubes.side.server.command;

import ethanjones.cubes.side.common.Cubes;

import java.util.List;

public class TimeCommand {

  public static void init() {
    CommandBuilder time = new CommandBuilder("time").register().add(CommandValue.intValue).setCommandPermission(CommandPermission.Extended).setCommandListener(new CommandListener() {
      @Override
      public void onCommand(CommandBuilder builder, List<CommandArgument> arguments, CommandSender sender) {
        int i = (int) arguments.get(1).get();
        Cubes.getServer().world.setTime(i);
      }
    });
  }
}
