package ethanjones.cubes.side.server.integrated;

import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.networking.socket.SocketMonitor;
import ethanjones.cubes.world.save.Save;

import java.util.ArrayList;
import java.util.List;

public class SingleplayerServer extends IntegratedServer {

  private SingleplayerClientIdentifier singleplayerClientIdentifier;

  public SingleplayerServer(Save save) {
    super(save);
  }

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
