package ethanjones.cubes.side.server.dedicated;

import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.networking.socket.SocketMonitor;
import ethanjones.cubes.side.server.CubesServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DedicatedServer extends CubesServer {

  private final HashMap<SocketMonitor, ClientIdentifier> clients = new HashMap<SocketMonitor, ClientIdentifier>();
  private final ArrayList<ClientIdentifier> disconnected = new ArrayList<ClientIdentifier>();
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

    synchronized (clients) {
      Iterator<ClientIdentifier> iterator = disconnected.iterator();
      while (iterator.hasNext()) {
        ClientIdentifier next = iterator.next();
        next.getPlayerManager().disconnected();
        clients.remove(next.getSocketMonitor());
        iterator.remove();
      }
    }
  }

  @Override
  public boolean isDedicated() {
    return true;
  }

  @Override
  public List<ClientIdentifier> getAllClients() {
    synchronized (clients) {
      List<ClientIdentifier> list = new ArrayList<ClientIdentifier>(clients.size());
      list.addAll(clients.values());
      return list;
    }
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
      ClientIdentifier clientIdentifier = clients.get(socketMonitor);
      if (clientIdentifier != null && !disconnected.contains(clientIdentifier)) disconnected.add(clientIdentifier);
    }
  }
}
