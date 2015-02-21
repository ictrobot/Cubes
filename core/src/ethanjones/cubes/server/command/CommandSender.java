package ethanjones.cubes.server.command;

public interface CommandSender {

  public void print(String string);

  public CommandPermission getPermissionLevel();
  
}
