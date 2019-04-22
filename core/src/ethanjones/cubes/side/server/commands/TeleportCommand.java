package ethanjones.cubes.side.server.commands;

import com.badlogic.gdx.math.Vector3;
import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.side.server.command.*;

import java.util.List;

public class TeleportCommand {

  public static void init() {
    CommandBuilder tp = new CommandBuilder("tp").register();
    tp.add(CommandValue.coordinateX).add(CommandValue.coordinateY).add(CommandValue.coordinateZ).setCommandListener(new CommandListener() {
      @Override
      public void onCommand(CommandBuilder builder, List<CommandArgument> arguments, CommandSender sender) {
        if (sender instanceof Player) {
          float x = (Float) arguments.get(1).get();
          float y = (Float) arguments.get(2).get();
          float z = (Float) arguments.get(3).get();
          ((Player) sender).clientIdentifier.getPlayerManager().setPosition(new Vector3(x, y, z), null, false);
          sender.print(Localization.get("command.teleport.coordinate", x, y, z));
        } else {
          sender.print(Localization.get("command.common.onlyPlayer"));
        }
      }
    });
    tp.add(CommandValue.clientIdentifier).setCommandListener(new CommandListener() {
      @Override
      public void onCommand(CommandBuilder builder, List<CommandArgument> arguments, CommandSender sender) {
        if (sender instanceof Player) {
          Player p = ((ClientIdentifier) arguments.get(1).get()).getPlayer();
          ((Player) sender).clientIdentifier.getPlayerManager().setPosition(p.position, p.angle, false);
          sender.print(Localization.get("command.teleport.player", p.username));
        } else {
          sender.print(Localization.get("command.common.onlyPlayer"));
        }
      }
    });
  }
  
}
