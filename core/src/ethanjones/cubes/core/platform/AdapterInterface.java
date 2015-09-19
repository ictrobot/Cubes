package ethanjones.cubes.core.platform;

import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.client.CubesClient;
import ethanjones.cubes.side.server.CubesServer;

import com.badlogic.gdx.ApplicationListener;

public interface AdapterInterface extends ApplicationListener {

  public CubesClient getClient();

  public void setClient(CubesClient cubesClient) throws UnsupportedOperationException;

  public CubesServer getServer();

  public void setServer(CubesServer cubesServer) throws UnsupportedOperationException;

  public Menu getMenu();

  public void setMenu(Menu menu) throws UnsupportedOperationException;

  public Side getSide();

  //Call Adapter.dispose()
  public void dispose();

  public Thread getThread();
}
