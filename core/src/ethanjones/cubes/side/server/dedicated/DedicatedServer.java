package ethanjones.cubes.side.server.dedicated;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.networking.socket.SocketMonitor;
import ethanjones.cubes.side.server.CubesServer;

public class DedicatedServer extends CubesServer {

  private final HashMap<SocketMonitor, ClientIdentifier> clients = new HashMap<SocketMonitor, ClientIdentifier>();
  private ConsoleCommandSender consoleCommandSender;

  @Override
  public void create() {
    super.create();
    consoleCommandSender = new ConsoleCommandSender();
  }

  @Override
  public void render() {
    super.render();
    consoleCommandSender.update();
  }

  @Override
  public boolean isDedicated() {
    return true;
  }

  @Override
  public ClientIdentifier getClient(SocketMonitor socketMonitor) {
    synchronized (clients) {
      return clients.get(socketMonitor);
    }
  }

  @Override
  public ClientIdentifier getClient(String username) {
    synchronized (clients) {
      for (ClientIdentifier clientIdentifier : clients.values()) {
        if (clientIdentifier.getPlayer().username.equals(username)) {
          return clientIdentifier;
        }
      }
    }
    return null;
  }

  @Override
  public void addClient(ClientIdentifier clientIdentifier) {
    synchronized (clients) {
      clients.put(clientIdentifier.getSocketMonitor(), clientIdentifier);
    }
  }

  @Override
  public void removeClient(SocketMonitor socketMonitor) {
    synchronized (clients) {
      clients.remove(socketMonitor);
    }
  }

  @Override
  public List<ClientIdentifier> getAllClients() {
    synchronized (clients) {
      List<ClientIdentifier> list = new ArrayList<ClientIdentifier>(clients.size());
      list.addAll(clients.values());
      return list;
    }
  }
}
