package ethanjones.cubes.side.server.command;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.networking.NetworkingManager;
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
          Player player = (Player) sender;
          float x = (Float) arguments.get(1).get();
          float y = (Float) arguments.get(2).get();
          float z = (Float) arguments.get(3).get();
          player.position.set(x, y, z);
          NetworkingManager.sendPacketToClient(new PacketPlayerMovement(player), player.clientIdentifier);
        } else {
          sender.print(Localization.get("command.common.onlyPlayer"));
        }
      }
    });
    tp.add(CommandValue.clientIdentifier).setCommandListener(new CommandListener() {
      @Override
      public void onCommand(CommandBuilder builder, List<CommandArgument> arguments, CommandSender sender) {
        if (sender instanceof Player) {
          PacketPlayerMovement packetPlayerMovement = new PacketPlayerMovement();
          packetPlayerMovement.angle = ((Player) sender).angle;
          packetPlayerMovement.position = new Vector3().set(((ClientIdentifier) arguments.get(1).get()).getPlayer().position);
          NetworkingManager.sendPacketToClient(packetPlayerMovement, ((Player) sender).clientIdentifier);
        } else {
          sender.print(Localization.get("command.common.onlyPlayer"));
        }
      }
    });
  }
  
}
