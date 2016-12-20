package ethanjones.cubes.side.server.dedicated;

import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.platform.ServerCmdLineOptions;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.networking.socket.SocketMonitor;
import ethanjones.cubes.side.server.CubesServer;
import ethanjones.cubes.world.save.Save;
import ethanjones.cubes.world.save.SaveOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DedicatedServer extends CubesServer {

  private final HashMap<SocketMonitor, ClientIdentifier> clients = new HashMap<SocketMonitor, ClientIdentifier>();
  private final ArrayList<ClientIdentifier> disconnected = new ArrayList<ClientIdentifier>();
  private ConsoleCommandSender consoleCommandSender;

  public DedicatedServer(ServerCmdLineOptions options) {
    super(getSave(options));
  }

  private static Save getSave(ServerCmdLineOptions options) {
    Save save = new Save("world", Compatibility.get().getBaseFolder().child("world"));
    if (save.readSaveOptions() == null) {
      SaveOptions saveOptions = save.getSaveOptions();
      if (options.worldSeed != null) saveOptions.worldSeed = options.worldSeed;
      if (options.worldGenerator != null) saveOptions.worldType = options.worldGenerator;
      if (options.worldGamemode != null) saveOptions.worldGamemode = options.worldGamemode;
    }
    return save;
  }

  @Override
  public void create() {
    super.create();
    consoleCommandSender = new ConsoleCommandSender();
    
    loop();
  }
  
  @Override
  protected void update() {
    super.update();
    
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
  protected void tick() {
    super.tick();
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
