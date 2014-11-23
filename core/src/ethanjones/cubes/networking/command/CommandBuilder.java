package ethanjones.cubes.networking.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandBuilder {

  private final CommandBuilder parent;
  private final ArrayList<CommandBuilder> children;
  private final List<CommandBuilder> childrenUnmodifiable;
  private final CommandValue commandValue;
  private final String commandString;
  private CommandListener commandListener;
  private String helpText;

  public CommandBuilder(String base) {
    this(null, base, null);
  }

  private CommandBuilder(CommandBuilder parent, String commandString) {
    this(parent, commandString, null);
  }

  private CommandBuilder(CommandBuilder parent, CommandValue commandValue) {
    this(parent, null, commandValue);
  }

  private CommandBuilder(CommandBuilder parent, String commandString, CommandValue commandValue) {
    this.parent = parent;
    this.children = new ArrayList<CommandBuilder>();
    this.childrenUnmodifiable = Collections.unmodifiableList(children);
    this.commandString = commandString;
    this.commandValue = commandValue;
  }

  public CommandBuilder getParent() {
    return parent;
  }

  public CommandValue getCommandValue() {
    return commandValue;
  }

  public String getCommandString() {
    return commandString;
  }

  public CommandBuilder setHelpText(String helpText) {
    this.helpText = helpText;
    return this;
  }

  public String getHelpText() {
    return helpText;
  }

  public CommandBuilder setCommandListener(CommandListener commandListener) {
    this.commandListener = commandListener;
    return this;
  }

  public CommandListener getCommandListener() {
    return commandListener;
  }

  public List<CommandBuilder> getChildren() {
    return childrenUnmodifiable;
  }

  public CommandBuilder add(String string) {
    CommandBuilder commandBuilder = new CommandBuilder(this, string);
    children.add(commandBuilder);
    return commandBuilder;
  }

  public CommandBuilder add(CommandValue value) {
    CommandBuilder commandBuilder = new CommandBuilder(this, value);
    children.add(commandBuilder);
    return commandBuilder;
  }

  public CommandBuilder register() {
    if (parent == null) {
      CommandManager.register(this);
    } else {
      parent.register();
    }
    return this;
  }
}
