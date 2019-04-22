package ethanjones.cubes.side.server.command;

import com.badlogic.gdx.math.Vector3;
import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.side.common.Cubes;

public abstract class CommandValue<T> {

  public static final CommandValue<String> stringValue = new CommandValue<String>() {
    @Override
    public String getArgument(String string, CommandSender sender) throws CommandParsingException {
      return string;
    }

    @Override
    public String toString() {
      return Localization.get("command.common.value.stringValue.string");
    }
  };

  public static final CommandValue<Float> floatValue = new CommandValue<Float>() {
    @Override
    public Float getArgument(String string, CommandSender sender) throws CommandParsingException {
      try {
        return Float.parseFloat(string);
      } catch (NumberFormatException e) {
        throw new CommandParsingException("command.common.value.floatValue.parsing");
      }
    }

    @Override
    public String toString() {
      return Localization.get("command.common.value.floatValue.string");
    }
  };

  public static final CommandValue<Integer> intValue = new CommandValue<Integer>() {
    @Override
    public Integer getArgument(String string, CommandSender sender) throws CommandParsingException {
      try {
        return Integer.parseInt(string);
      } catch (NumberFormatException e) {
        throw new CommandParsingException("command.common.value.intValue.parsing");
      }
    }

    @Override
    public String toString() {
      return Localization.get("command.common.value.intValue.string");
    }
  };

  private static abstract class Coordinate extends CommandValue<Float> {
    @Override
    public Float getArgument(String string, CommandSender sender) throws CommandParsingException {
      try {
        if (string.startsWith("@")) {
          Vector3 location = null;
          try {
            location = sender.getLocation();
          } catch (UnsupportedOperationException ignored) {}
          if (location == null) throw new CommandParsingException("command.common.onlyPlayer");

          if (string.length() == 1) return getComponent(location);
          return getComponent(location) + Float.parseFloat(string.substring(1));
        }
        return Float.parseFloat(string);
      } catch (NumberFormatException e) {
        throw new CommandParsingException("command.common.value.coordinate.parsing");
      }
    }

    protected abstract float getComponent(Vector3 v);

    @Override
    public String toString() {
      return Localization.get("command.common.value.coordinate.string");
    }
  }

  public static final CommandValue<Float> coordinateX = new Coordinate() {
    @Override
    protected float getComponent(Vector3 v) {
      return v.x;
    }
  };

  public static final CommandValue<Float> coordinateY = new Coordinate() {
    @Override
    protected float getComponent(Vector3 v) {
      return v.y;
    }
  };

  public static final CommandValue<Float> coordinateZ = new Coordinate() {
    @Override
    protected float getComponent(Vector3 v) {
      return v.z;
    }
  };

  public static final CommandValue<ClientIdentifier> clientIdentifier = new CommandValue<ClientIdentifier>() {
    @Override
    public ClientIdentifier getArgument(String string, CommandSender sender) throws CommandParsingException {
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
    public CommandBuilder getArgument(String string, CommandSender sender) throws CommandParsingException {
      CommandBuilder commandBuilder = CommandManager.commands.get(string);
      if (commandBuilder == null) throw new CommandParsingException("command.common.value.command.parsing");
      return commandBuilder;
    }

    @Override
    public String toString() {
      return Localization.get("command.common.value.command.string");
    }
  };

  public abstract T getArgument(String string, CommandSender sender) throws CommandParsingException;

  public abstract String toString();

}
