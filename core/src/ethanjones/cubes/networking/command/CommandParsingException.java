package ethanjones.cubes.networking.command;

public class CommandParsingException extends Exception {

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
