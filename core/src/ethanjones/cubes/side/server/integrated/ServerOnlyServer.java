package ethanjones.cubes.side.server.integrated;

import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.networking.socket.SocketMonitor;

import java.util.*;

public class ServerOnlyServer extends IntegratedServer {

  private final HashMap<SocketMonitor, ClientIdentifier> clients = new HashMap<SocketMonitor, ClientIdentifier>();
  private final HashSet<ClientIdentifier> disconnected = new HashSet<ClientIdentifier>();

  @Override
  public void render() {
    super.render();

    Iterator<ClientIdentifier> iterator = disconnected.iterator();
    while (iterator.hasNext()) {
      ClientIdentifier next = iterator.next();
      next.getPlayerManager().disconnected();
      iterator.remove();
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
      if (clientIdentifier != null) disconnected.add(clientIdentifier);
    }
  }

}
