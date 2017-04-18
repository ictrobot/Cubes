package ethanjones.cubes.side.server.integrated;

import ethanjones.cubes.core.gwt.Task;
import ethanjones.cubes.core.gwt.Task.TimelimitException;
import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.networking.socket.SocketMonitor;
import ethanjones.cubes.side.common.Side;
import ethanjones.cubes.side.server.CubesServer;
import ethanjones.cubes.world.save.Save;

import java.util.ArrayList;
import java.util.List;

public class IntegratedServer extends CubesServer implements Runnable {

  public final Task task;

  public IntegratedServer(Save save) {
    super(save);
    this.task = new Task(this);
    this.task.setName("Server");
    this.task.setAbbreviation("S");
    this.task.setSide(Side.Server);
  }

  public void start() {
    task.start();
  }

  @Override
  public void run() {
    if (!state.isSetup()) {
      try {
        create();
      } catch (Exception e) {
        Debug.crash(e);
      }
    }

    if (state.isStopping()) {
      try {
        stop();
      } catch (Exception e) {
        Debug.crash(e);
      }
      return;
    }

    try {
      loop();
      stop();
    } catch (Exception e) {
      if (e instanceof Task.TimelimitException) {
        throw (TimelimitException) e;
      } else {
        Debug.crash(e);
      }
    }
  }

  @Override
  protected void update() {
    super.update();
    task.checkTime();
  }

  @Override
  public boolean isDedicated() {
    return false;
  }

  private SingleplayerClientIdentifier singleplayerClientIdentifier;

  public void create() {
    super.create();
    singleplayerClientIdentifier = new SingleplayerClientIdentifier();
  }

  @Override
  public List<ClientIdentifier> getAllClients() {
    List<ClientIdentifier> list = new ArrayList<ClientIdentifier>(1);
    if (singleplayerClientIdentifier != null) list.add(singleplayerClientIdentifier); // don't add if not yet created
    return list;
  }

  @Override
  public ClientIdentifier getClient(SocketMonitor socketMonitor) {
    return singleplayerClientIdentifier;
  }

  @Override
  public ClientIdentifier getClient(String username) {
    if (username.equals(singleplayerClientIdentifier.getPlayer().username)) return singleplayerClientIdentifier;
    return null;
  }

  @Override
  public void addClient(ClientIdentifier clientIdentifier) {

  }

  @Override
  public void removeClient(SocketMonitor socketMonitor) {

  }

}
