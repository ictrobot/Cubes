package ethanjones.cubes.core.adapter;

import com.badlogic.gdx.ApplicationListener;

import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.core.system.Memory;
import ethanjones.cubes.networking.server.ServerNetworkingParameter;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.server.CubesServer;

public class HeadlessAdapter implements ApplicationListener {

  CubesServer cubesServer;

  @Override
  public void create() {
    try {
      Thread.currentThread().setName(Side.Server.name());
      Cubes.setup();
      cubesServer = new CubesServer(new ServerNetworkingParameter());
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
    try {
      Memory.update();
      cubesServer.render();
    } catch (Exception e) {
      Debug.crash(e);
    }
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
      cubesServer.dispose();
    } catch (Exception e) {
      Debug.crash(e);
    }
  }
}
