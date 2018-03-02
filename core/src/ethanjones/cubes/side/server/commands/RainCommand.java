package ethanjones.cubes.side.server.commands;

import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.server.command.*;
import ethanjones.cubes.world.generator.RainStatus;
import ethanjones.cubes.world.server.WorldServer;

import java.util.List;

public class RainCommand {

  public static void init() {
    CommandBuilder rain = new CommandBuilder("rain").register().setCommandPermission(CommandPermission.Extended);
    rain.add("disable").setCommandListener(new CommandListener() {
      @Override
      public void onCommand(CommandBuilder builder, List<CommandArgument> arguments, CommandSender sender) {
        ((WorldServer) Cubes.getServer().world).overrideRainStatus(RainStatus.NOT_RAINING, 60*60*24);
      }
    }).add(CommandValue.intValue).setCommandListener(new CommandListener() {
      @Override
      public void onCommand(CommandBuilder builder, List<CommandArgument> arguments, CommandSender sender) {
        ((WorldServer) Cubes.getServer().world).overrideRainStatus(RainStatus.NOT_RAINING, (Integer) arguments.get(2).get());
      }
    });
    rain.add("enable").setCommandListener(new CommandListener() {
      @Override
      public void onCommand(CommandBuilder builder, List<CommandArgument> arguments, CommandSender sender) {
        ((WorldServer) Cubes.getServer().world).removeRainStatusOverride();
      }
    });
    rain.add("override").add(CommandValue.floatValue).add(CommandValue.intValue).setCommandListener(new CommandListener() {
      @Override
      public void onCommand(CommandBuilder builder, List<CommandArgument> arguments, CommandSender sender) {
        float r = (Float) arguments.get(2).get();
        RainStatus rainStatus;
        if (r == 0) {
          rainStatus = RainStatus.NOT_RAINING;
        } else {
          rainStatus = new RainStatus(true, r);
        }
        ((WorldServer) Cubes.getServer().world).overrideRainStatus(rainStatus, (Integer) arguments.get(3).get());
      }
    });
  }
  
}
