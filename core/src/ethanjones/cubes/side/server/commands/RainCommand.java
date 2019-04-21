package ethanjones.cubes.side.server.commands;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.server.command.*;
import ethanjones.cubes.world.generator.RainStatus;
import ethanjones.cubes.world.server.WorldServer;

import com.badlogic.gdx.math.MathUtils;

import java.util.List;

public class RainCommand {

  public static void init() {
    CommandBuilder rain = new CommandBuilder("rain").register().setCommandPermission(CommandPermission.Extended);
    rain.add("disable").setCommandListener(new CommandListener() {
      @Override
      public void onCommand(CommandBuilder builder, List<CommandArgument> arguments, CommandSender sender) {
        int time = 60*60*24;
        ((WorldServer) Cubes.getServer().world).overrideRainStatus(RainStatus.NOT_RAINING, time);
        sender.print(Localization.get("command.rain.disable", time));
      }
    }).add(CommandValue.intValue).setCommandListener(new CommandListener() {
      @Override
      public void onCommand(CommandBuilder builder, List<CommandArgument> arguments, CommandSender sender) {
        int time = (Integer) arguments.get(2).get();
        ((WorldServer) Cubes.getServer().world).overrideRainStatus(RainStatus.NOT_RAINING, time);
        sender.print(Localization.get("command.rain.disable", time));
      }
    });
    rain.add("enable").setCommandListener(new CommandListener() {
      @Override
      public void onCommand(CommandBuilder builder, List<CommandArgument> arguments, CommandSender sender) {
        ((WorldServer) Cubes.getServer().world).removeRainStatusOverride();
        sender.print(Localization.get("command.rain.enable"));
      }
    });
    rain.add("override").add(CommandValue.floatValue).add(CommandValue.intValue).setCommandListener(new CommandListener() {
      @Override
      public void onCommand(CommandBuilder builder, List<CommandArgument> arguments, CommandSender sender) {
        float r = MathUtils.clamp((Float) arguments.get(2).get(), 0, 1);
        RainStatus rainStatus;
        if (r == 0) {
          rainStatus = RainStatus.NOT_RAINING;
        } else {
          rainStatus = new RainStatus(true, r);
        }
        int time = (Integer) arguments.get(3).get();
        ((WorldServer) Cubes.getServer().world).overrideRainStatus(rainStatus, time);
        sender.print(Localization.get("command.rain.override", r, time));
      }
    });
  }
  
}
