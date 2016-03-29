package ethanjones.cubes.networking.packets;

import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.side.common.Cubes;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import static com.badlogic.gdx.Input.Buttons.*;

public class PacketButton extends Packet {

  public static final int BUTTON_DOWN = 0;
  public static final int BUTTON_UP = 1;

  public int button;
  public int action;

  @Override
  public void write(DataOutputStream dataOutputStream) throws Exception {
    dataOutputStream.writeInt(button);
    dataOutputStream.writeInt(action);
  }

  @Override
  public void read(DataInputStream dataInputStream) throws Exception {
    button = dataInputStream.readInt();
    action = dataInputStream.readInt();
  }

  @Override
  public void handlePacket() {
    Cubes.getServer().getClient(getSocketMonitor()).getPlayerManager().handlePacket(this);
  }

  @Override
  public String toString() {
    return super.toString() + " " + buttonName() + (action == BUTTON_DOWN ? " down" : (action == BUTTON_UP ? " up" : " " + action));
  }

  public String buttonName() {
    switch (button) {
      case LEFT:
        return "left";
      case RIGHT:
        return "right";
      case MIDDLE:
        return "middle";
      case BACK:
        return "back";
      case FORWARD:
        return "forward";
      default:
        return "" + button;
    }
  }
}
