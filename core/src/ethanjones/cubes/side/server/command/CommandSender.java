package ethanjones.cubes.side.server.command;

import com.badlogic.gdx.math.Vector3;

public interface CommandSender {

  public void print(String string);

  public CommandPermission getPermissionLevel();

  public Vector3 getLocation() throws UnsupportedOperationException;
  
}
