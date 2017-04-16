package ethanjones.cubes.core.platform;

import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.side.client.CubesClient;
import ethanjones.cubes.side.common.Side;
import ethanjones.cubes.side.server.CubesServer;

import com.badlogic.gdx.ApplicationListener;

public interface AdapterInterface extends ApplicationListener {

  CubesClient getClient();

  void setClient(CubesClient cubesClient) throws UnsupportedOperationException;

  CubesServer getServer();

  void setServer(CubesServer cubesServer) throws UnsupportedOperationException;

  Menu getMenu();

  void setMenu(Menu menu) throws UnsupportedOperationException;

  Side getSide();

  //Call Adapter.dispose()
  void dispose();
}
