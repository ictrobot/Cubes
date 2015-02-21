package ethanjones.cubes.server.command;

import ethanjones.cubes.common.localization.Localization;
import ethanjones.cubes.common.networking.server.ClientIdentifier;
import ethanjones.cubes.Cubes;

public abstract class CommandValue<T> {

  //Localise exceptions, strings, help messages and everything command related
  public static final CommandValue<Float> coordinate = new CommandValue<Float>() {
    @Override
    public Float getArgument(String string) throws CommandParsingException {
      try {
        return Float.parseFloat(string);
      } catch (NumberFormatException e) {
        throw new CommandParsingException("commands.common.value.coordinate.parsing");
      }
    }

    @Override
    public String toString() {
      return Localization.get("command.common.value.coordinate.string");
    }
  };
  public static final CommandValue<Integer> blockCoordinate = new CommandValue<Integer>() {
    @Override
    public Integer getArgument(String string) throws CommandParsingException {
      try {
        return Integer.parseInt(string);
      } catch (NumberFormatException e) {
        throw new CommandParsingException("commands.common.value.blockCoordinate.parsing");
      }
    }

    @Override
    public String toString() {
      return Localization.get("command.common.value.blockCoordinate.string");
    }
  };
  public static final CommandValue<ClientIdentifier> clientIdentifier = new CommandValue<ClientIdentifier>() {
    @Override
    public ClientIdentifier getArgument(String string) throws CommandParsingException {
      ClientIdentifier client = Cubes.getServer().getClient(string);
      if (client == null) throw new CommandParsingException("commands.common.value.clientIdentifier.parsing");
      return client;
    }

    @Override
    public String toString() {
      return Localization.get("command.common.value.clientIdentifier.string");
    }
  };
  public static final CommandValue<CommandBuilder> command = new CommandValue<CommandBuilder>() {
    @Override
    public CommandBuilder getArgument(String string) throws CommandParsingException {
      CommandBuilder commandBuilder = CommandManager.commands.get(string);
      if (commandBuilder == null) throw new CommandParsingException("commands.common.value.command.parsing");
      return commandBuilder;
    }

    @Override
    public String toString() {
      return Localization.get("command.common.value.command.string");
    }
  };

  public abstract T getArgument(String string) throws CommandParsingException;

  public abstract String toString();

}
