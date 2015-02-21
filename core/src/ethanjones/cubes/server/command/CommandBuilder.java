package ethanjones.cubes.server.command;

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
  private CommandPermission commandPermission;
  private String helpText;

  public CommandBuilder(String base) {
    this(null, base, null);
  }

  private CommandBuilder(CommandBuilder parent, String commandString, CommandValue commandValue) {
    this.parent = parent;
    this.children = new ArrayList<CommandBuilder>();
    this.childrenUnmodifiable = Collections.unmodifiableList(children);
    this.commandString = commandString;
    this.commandValue = commandValue;
    this.commandPermission = parent != null ? parent.getCommandPermission() : CommandPermission.Basic; //inherit command permission
  }

  public CommandPermission getCommandPermission() {
    return commandPermission;
  }

  public CommandBuilder setCommandPermission(CommandPermission commandPermission) {
    this.commandPermission = commandPermission;
    return this;
  }

  private CommandBuilder(CommandBuilder parent, String commandString) {
    this(parent, commandString, null);
  }

  private CommandBuilder(CommandBuilder parent, CommandValue commandValue) {
    this(parent, null, commandValue);
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

  public String getHelpText() {
    return helpText;
  }

  public CommandBuilder setHelpText(String helpText) {
    this.helpText = helpText;
    return this;
  }

  public CommandListener getCommandListener() {
    return commandListener;
  }

  public CommandBuilder setCommandListener(CommandListener commandListener) {
    this.commandListener = commandListener;
    return this;
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
