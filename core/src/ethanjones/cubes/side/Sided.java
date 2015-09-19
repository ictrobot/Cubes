package ethanjones.cubes.side;

import ethanjones.cubes.block.BlockManager;
import ethanjones.cubes.core.event.EventBus;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.system.CubesSecurity;
import ethanjones.cubes.core.timing.Timing;
import ethanjones.cubes.networking.Networking;
import ethanjones.cubes.networking.NetworkingManager;

public class Sided {

  private static class SidedData {
    EventBus eventBus;
    Timing timing;
    BlockManager blockManager;
  }

  private static SidedData clientData;
  private static SidedData serverData;
  private static ThreadLocal<Side> sideLocal = new ThreadLocal<Side>();
  private static ThreadLocal<Boolean> mainLocal = new ThreadLocal<Boolean>() {
    @Override
    public Boolean initialValue() {
      return false;
    }
  };

  public static EventBus getEventBus() {
    return getData().eventBus;
  }

  private static SidedData getData() {
    Side side = getSide();
    if (side == null) {
      throw new CubesException("Sided objects cannot be accessed from thread: " + Thread.currentThread().getName());
    }
    SidedData data = getData(side);
    if (data == null) throw new CubesException("Sided objects are not setup");
    return data;
  }

  public static Side getSide() {
    return sideLocal.get();
  }

  /**
   * Allow network threads etc. to access sided objects
   */
  public static void setSide(Side side) {
    if (mainLocal.get()) return;
    sideLocal.set(side);
  }

  private static SidedData getData(Side side) {
    switch (side) {
      case Client:
        return clientData;
      case Server:
        return serverData;
    }
    return null;
  }

  public static Timing getTiming() {
    return getData().timing;
  }

  public static BlockManager getBlockManager() {
    return getData().blockManager;
  }

  public static Networking getNetworking() {
    return NetworkingManager.getNetworking(getSide());
  }

  public static void setup(Side side) {
    CubesSecurity.checkSidedSetup();

    if (side == null || getData(side) != null) return;

    sideLocal.set(side);
    mainLocal.set(true);

    SidedData data = new SidedData();
    switch (side) {
      case Client:
        clientData = data;
        break;
      case Server:
        serverData = data;
        break;
    }

    data.eventBus = new EventBus();
    data.timing = new Timing();
    data.blockManager = new BlockManager();
    if (side == Side.Server) data.blockManager.generateDefault(); //TODO Need to store with world
  }

  public static boolean isSetup(Side side) {
    return side != null && getData(side) != null;
  }

  public static void reset(Side side) {
    CubesSecurity.checkSidedReset();

    if (side == null) return;

    switch (side) {
      case Client:
        clientData = null;
        break;
      case Server:
        serverData = null;
        break;
    }
  }
}
