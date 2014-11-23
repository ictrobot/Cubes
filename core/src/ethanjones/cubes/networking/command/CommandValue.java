package ethanjones.cubes.networking.command;

import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.side.common.Cubes;

public abstract class CommandValue<T> {

  public abstract T getArgument(String string) throws CommandParsingException;

  public final CommandValue<Double> coordinate = new CommandValue<Double>() {
    @Override
    public Double getArgument(String string) throws CommandParsingException {
      try {
        return Double.parseDouble(string);
      } catch (NumberFormatException e) {
        throw new CommandParsingException("Not a number");
      }
    }
  };

  public final CommandValue<Integer> blockCoordinate = new CommandValue<Integer>() {
    @Override
    public Integer getArgument(String string) throws CommandParsingException {
      try {
        return Integer.parseInt(string);
      } catch (NumberFormatException e) {
        throw new CommandParsingException("Not a integer");
      }
    }
  };

  public final CommandValue<ClientIdentifier> clientIdentifier = new CommandValue<ClientIdentifier>() {
    @Override
    public ClientIdentifier getArgument(String string) throws CommandParsingException {
      return Cubes.getServer().getClient(string);
    }
  };

}
