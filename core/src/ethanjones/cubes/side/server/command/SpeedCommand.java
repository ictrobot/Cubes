package ethanjones.cubes.side.server.command;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packets.PacketMovementSpeed;

import java.util.List;

public class SpeedCommand {

  public static void init() {
    CommandBuilder speed = new CommandBuilder("speed").register().add(CommandValue.coordinate).setCommandListener(new CommandListener() {
      @Override
      public void onCommand(CommandBuilder builder, List<CommandArgument> arguments, CommandSender sender) {
        if (sender instanceof Player) {
          float f = (float) arguments.get(1).get();
          PacketMovementSpeed p = new PacketMovementSpeed();
          p.speed = f;
          NetworkingManager.sendPacketToClient(p, ((Player) sender).clientIdentifier);
        } else {
          sender.print(Localization.get("command.common.onlyPlayer"));
        }
      }
    });
  }
}
