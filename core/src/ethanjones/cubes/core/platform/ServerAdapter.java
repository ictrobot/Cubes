package ethanjones.cubes.core.platform;

import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.server.ServerNetworkingParameter;
import ethanjones.cubes.side.client.CubesClient;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.common.Side;
import ethanjones.cubes.side.server.CubesServer;
import ethanjones.cubes.side.server.dedicated.DedicatedServer;

public class ServerAdapter implements AdapterInterface {

  private DedicatedServer cubesServer;
  private ServerCmdLineOptions options;
  private Thread thread;

  public ServerAdapter() {
    Adapter.setInterface(this);
  }

  @Override
  public void create() {
    try {
      thread = Thread.currentThread();
      thread.setName(getSide().name());

      options = new ServerCmdLineOptions();
      options.parse();

      Cubes.setup(this);
      if (options.port != null) {
        NetworkingManager.serverPreInit(new ServerNetworkingParameter(options.port));
      } else {
        NetworkingManager.serverPreInit(new ServerNetworkingParameter());
      }
      cubesServer = new DedicatedServer(options);
      cubesServer.create();
    } catch (Exception e) {
      Debug.crash(e);
    }
  }

  @Override
  public void resize(int width, int height) {

  }

  @Override
  public void render() {
    // Uses internal loop
    throw new UnsupportedOperationException();
  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  @Override
  public void dispose() {
    try {
      Adapter.dispose();
    } catch (StopLoopException ignored) {

    }
  }

  @Override
  public Thread getThread() {
    return thread;
  }

  @Override
  public void setClient(CubesClient cubesClient) throws UnsupportedOperationException {
    //throw new UnsupportedOperationException("Cannot set client");
  }

  @Override
  public void setServer(CubesServer cubesServer) throws UnsupportedOperationException {
    //throw new UnsupportedOperationException("Cannot set server");
  }

  @Override
  public void setMenu(Menu menu) throws UnsupportedOperationException {
    //throw new UnsupportedOperationException("Cannot set menu");
  }

  @Override
  public CubesClient getClient() {
    return null;
  }

  @Override
  public CubesServer getServer() {
    return cubesServer;
  }

  @Override
  public Menu getMenu() {
    return null;
  }

  @Override
  public Side getSide() {
    return Side.Server;
  }
}
