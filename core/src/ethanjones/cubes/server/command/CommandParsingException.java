package ethanjones.cubes.server.command;

import ethanjones.cubes.common.core.localization.LocalizedException;

public class CommandParsingException extends LocalizedException {

  public CommandParsingException(String arg0) {
    super(arg0);
  }

  public CommandParsingException(Throwable arg0) {
    super(arg0);
  }

  public CommandParsingException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }
}
