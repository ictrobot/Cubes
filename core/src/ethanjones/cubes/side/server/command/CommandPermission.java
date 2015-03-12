package ethanjones.cubes.side.server.command;

public enum CommandPermission {
  None, Basic, Extended, All;

  public static boolean check(CommandPermission required, CommandPermission permission) {
    return permission.ordinal() > required.ordinal() || permission.ordinal() == required.ordinal();
  }
}
