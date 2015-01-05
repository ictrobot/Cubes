package ethanjones.cubes.core.platform;

import com.badlogic.gdx.ApplicationListener;

import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.client.CubesClient;
import ethanjones.cubes.side.server.CubesServer;

public interface AdapterInterface extends ApplicationListener {

  public void setClient(CubesClient cubesClient) throws UnsupportedOperationException;

  public void setServer(CubesServer cubesServer) throws UnsupportedOperationException;

  public void setMenu(Menu menu) throws UnsupportedOperationException;

  public CubesClient getClient();

  public CubesServer getServer();

  public Menu getMenu();

  public Side getSide();

  //Call Adapter.dispose()
  public void dispose();

  public Thread getThread();
}
