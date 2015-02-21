package ethanjones.cubes.common.networking.client;

public class PingResult {

  public int serverMajor;
  public int serverMinor;
  public int serverPoint;
  public int serverBuild;
  public String serverHash;
  public String[] players;
  public int ping;
  public boolean failure = false;
  public Exception exception;

  public String toString() {
    if (failure) {
      return exception.getClass().getSimpleName() + ": " + (exception.getMessage() != null ? exception.getMessage() : "");
    } else {
      StringBuilder builder = new StringBuilder(ping + "ms v" + serverMajor + "." + serverMinor + "." + serverPoint + "." + serverBuild + " " + serverHash + " [");
      for (String player : players) {
        builder.append(player).append(" ");
      }
      builder.append("]");
      return builder.toString();
    }
  }
}
