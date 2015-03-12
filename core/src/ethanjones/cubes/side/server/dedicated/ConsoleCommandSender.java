package ethanjones.cubes.side.server.dedicated;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.side.server.command.CommandManager;
import ethanjones.cubes.side.server.command.CommandPermission;
import ethanjones.cubes.side.server.command.CommandSender;

public class ConsoleCommandSender implements CommandSender {

  private static class ThreadedReader extends Thread {

    final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    final ArrayList<String> list = new ArrayList<String>();

    public ThreadedReader() {
      setName("ThreadedReader");
      setDaemon(true);
      setUncaughtExceptionHandler(Debug.UncaughtExceptionHandler.instance);
    }

    public void run() {
      String line = null;
      try {
        while ((line = reader.readLine()) != null) {
          synchronized (list) {
            list.add(line);
          }
        }
      } catch (IOException e) {

      }
    }

    public String getLine() {
      synchronized (list) {
        if (list.size() > 0) return list.remove(0);
      }
      return null;
    }
  }
  ThreadedReader threadedReader = new ThreadedReader();

  protected ConsoleCommandSender() {
    threadedReader.start();
  }

  @Override
  public void print(String string) {
    Log.info("[" + Localization.get("server.console_capitals") + "] " + string);
  }

  @Override
  public CommandPermission getPermissionLevel() {
    return CommandPermission.All;
  }

  protected void update() {
    String string = threadedReader.getLine();
    if (string != null) {
      CommandManager.run(string, this);
    }
  }

}
