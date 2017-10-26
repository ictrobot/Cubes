package ethanjones.cubes.side.common;

import ethanjones.cubes.core.event.EventBus;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.timing.Timing;
import ethanjones.cubes.networking.Networking;
import ethanjones.cubes.networking.NetworkingManager;

public enum Side {
  Client, Server;
  
  private static SideData clientData;
  private static SideData serverData;
  private static ThreadLocal<Side> sideLocal = new ThreadLocal<Side>();
  private static ThreadLocal<Boolean> mainLocal = new ThreadLocal<Boolean>() {
    @Override
    public Boolean initialValue() {
      return false;
    }
  };
  
  public static EventBus getSidedEventBus() {
    return getData().eventBus;
  }
  
  public static Side getSide() {
    return sideLocal.get();
  }
  
  public static boolean isClient() {
    return sideLocal.get() == Client;
  }
  
  public static boolean isServer() {
    return sideLocal.get() == Server;
  }
  
  public static boolean isMainThread(Side side) {
    return mainLocal.get() && sideLocal.get() == side;
  }
  
  /**
   * Allow network threads etc. to access sided objects
   */
  public static void setSide(Side side) {
    if (mainLocal.get()) return;
    sideLocal.set(side);
  }
  
  private static SideData getData(Side side) {
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
  
  public static Networking getNetworking() {
    return NetworkingManager.getNetworking(getSide());
  }
  
  public static Cubes getCubes() {
    switch (getSide()) {
      case Client:
        return Cubes.getClient();
      case Server:
        return Cubes.getServer();
    }
    throw new IllegalStateException();
  }
  
  public static boolean isSetup(Side side) {
    return side != null && getData(side) != null;
  }
  
  static void setup(Side side) {
    if (side == null || getData(side) != null) return;
  
    sideLocal.set(side);
    mainLocal.set(true);
  
    SideData data = new SideData();
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
  }
  
  static void reset(Side side) {
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
  
  private static SideData getData() {
    Side side = getSide();
    if (side == null) {
      throw new CubesException("Sided objects cannot be accessed from thread: " + Thread.currentThread().getName());
    }
    SideData data = getData(side);
    if (data == null) throw new CubesException("Sided objects are not setup");
    return data;
  }
  
  private static class SideData {
    EventBus eventBus;
    Timing timing;
  }
}
