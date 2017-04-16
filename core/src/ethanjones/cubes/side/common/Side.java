package ethanjones.cubes.side.common;

import ethanjones.cubes.core.event.EventBus;
import ethanjones.cubes.core.gwt.Task;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.timing.Timing;
import ethanjones.cubes.networking.Networking;
import ethanjones.cubes.networking.NetworkingManager;

import java.util.Objects;

public enum Side {
  Client, Server;
  
  private static SideData clientData;
  private static SideData serverData;
  
  public static EventBus getEventBus() {
    return getData().eventBus;
  }
  
  public static Side getSide() {
    Side s = Task.getCurrentSide();
    return s == null ? Client : s;
  }
  
  public static boolean isClient() {
    return getSide() == Client;
  }
  
  public static boolean isServer() {
    return getSide() == Server;
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
      throw new CubesException("Current side is null");
    }
    SideData data = getData(side);
    if (data == null) throw new CubesException("Sided objects are not setup for " + Objects.toString(side));
    return data;
  }
  
  private static class SideData {
    EventBus eventBus;
    Timing timing;
  }
}
