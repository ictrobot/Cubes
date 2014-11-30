package ethanjones.cubes.side.server.command;

public class CommandArgument<T> {

  private final T obj;
  private final CommandValue<T> commandValue;

  public CommandArgument(T obj, CommandValue<T> commandValue) {
    if (obj == null) throw new NullPointerException();
    this.obj = obj;
    this.commandValue = commandValue;
  }

  public T get() {
    return obj;
  }

  public CommandValue<T> getCommandValue() {
    return commandValue;
  }

  public String toString() {
    return commandValue.toString();
  }
}
