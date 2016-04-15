package ethanjones.cubes.side.server.command;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packets.PacketOtherPlayerMovement;
import ethanjones.cubes.networking.packets.PacketPlayerMovement;
import ethanjones.cubes.networking.server.ClientIdentifier;

import com.badlogic.gdx.math.Vector3;

import java.util.List;

public class TeleportCommand {

  public static void init() {
    CommandBuilder tp = new CommandBuilder("tp").register();
    tp.add(CommandValue.coordinate).add(CommandValue.coordinate).add(CommandValue.coordinate).setCommandListener(new CommandListener() {
      @Override
      public void onCommand(CommandBuilder builder, List<CommandArgument> arguments, CommandSender sender) {
        if (sender instanceof Player) {
          float x = (Float) arguments.get(1).get();
          float y = (Float) arguments.get(2).get();
          float z = (Float) arguments.get(3).get();
          ((Player) sender).clientIdentifier.getPlayerManager().setPosition(new Vector3(x, y, z), null, false);
        } else {
          sender.print(Localization.get("command.common.onlyPlayer"));
        }
      }
    });
    tp.add(CommandValue.clientIdentifier).setCommandListener(new CommandListener() {
      @Override
      public void onCommand(CommandBuilder builder, List<CommandArgument> arguments, CommandSender sender) {
        if (sender instanceof Player) {
          Player tp = ((ClientIdentifier) arguments.get(1).get()).getPlayer();
          ((Player) sender).clientIdentifier.getPlayerManager().setPosition(tp.position, tp.angle, false);
        } else {
          sender.print(Localization.get("command.common.onlyPlayer"));
        }
      }
    });
  }
  
}
