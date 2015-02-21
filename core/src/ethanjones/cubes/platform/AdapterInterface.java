package ethanjones.cubes.platform;

import com.badlogic.gdx.ApplicationListener;

import ethanjones.cubes.client.graphics.menu.Menu;
import ethanjones.cubes.common.Side;
import ethanjones.cubes.client.CubesClient;
import ethanjones.cubes.server.CubesServer;

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
