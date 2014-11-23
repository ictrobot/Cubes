package ethanjones.cubes.networking.command;

public class CommandArgument<T> {

  private final T obj;
  private final CommandValue<T> commandValue;

  public CommandArgument(T obj, CommandValue<T> commandValue) {
    this.obj = obj;
    this.commandValue = commandValue;
  }

  public T get() {
    return obj;
  }

  public CommandValue<T> getCommandValue() {
    return commandValue;
  }
}
