package ethanjones.cubes.world.server;

public class WorldServerMaintenance implements Runnable {

  private final WorldServer worldServer;

  public WorldServerMaintenance(WorldServer worldServer) {
    this.worldServer = worldServer;
  }

  @Override
  public void run() {
    //TODO implement unloading and loading
  }
}
