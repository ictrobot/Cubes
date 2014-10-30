package ethanjones.cubes.networking.packet;

import java.util.HashMap;

import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.networking.packets.PacketID;
import ethanjones.cubes.networking.socket.SocketMonitor;
import ethanjones.cubes.side.Side;

public class PacketIDDatabase {

  private static enum State {
    Waiting, Confirmed
  }
  private final Object lock;
  int i;
  private HashMap<Integer, Class<? extends Packet>> intClass;
  private HashMap<Class<? extends Packet>, Integer> classInt;
  private HashMap<Class<? extends Packet>, State> states;

  public PacketIDDatabase() {
    intClass = new HashMap<Integer, Class<? extends Packet>>();
    classInt = new HashMap<Class<? extends Packet>, Integer>();
    states = new HashMap<Class<? extends Packet>, State>();
    lock = new Object();
    i = 0;

    synchronized (lock) {
      intClass.put(0, PacketID.class);
      classInt.put(PacketID.class, 0);
      states.put(PacketID.class, State.Confirmed);
    }
  }

  public boolean contains(Class<? extends Packet> c) {
    synchronized (lock) {
      return intClass.containsValue(c) && states.get(c) == State.Confirmed;
    }
  }

  public int get(Class<? extends Packet> c) {
    Integer integer;
    synchronized (lock) {
      integer = classInt.get(c);
    }
    if (integer == null) throw new CubesException("Unregistered packet type: " + c);
    return integer;
  }

  public Class<? extends Packet> get(int i) {
    Class<? extends Packet> packetClass;
    synchronized (lock) {
      packetClass = intClass.get(i);
    }
    if (packetClass == null) throw new CubesException("Unregistered packet type: " + i);
    return packetClass;
  }

  public void process(PacketID packetID) {
    if (packetID.c == null) return;
    Class<? extends Packet> c = getPacketClass(packetID.c);
    if (packetID.getPacketEnvironment().getReceiving().getSocketMonitor().getNetworking().getSide() == Side.Client) {
      synchronized (lock) {
        if (states.get(c) == State.Confirmed) return;
        intClass.put(packetID.id, c);
        classInt.put(c, packetID.id);
      }
      PacketID confirming = new PacketID();
      confirming.id = packetID.id;
      confirming.c = packetID.c;
      packetID.getPacketEnvironment().getReceiving().getSocketMonitor().queue(confirming);
    }
    synchronized (lock) {
      states.put(c, State.Confirmed);
    }
  }

  private static Class<? extends Packet> getPacketClass(String c) {
    try {
      return Class.forName(c).asSubclass(Packet.class);
    } catch (Exception e) {
      throw new CubesException("Invalid Packet Class", e);
    }
  }

  public void sendID(Class<? extends Packet> packetClass, SocketMonitor socketMonitor) {
    if (packetClass == null) return;
    synchronized (lock) {
      if (classInt.containsKey(packetClass) && states.get(packetClass) == State.Confirmed) return;
    }
    PacketID packetID = new PacketID();
    packetID.c = packetClass.getName();
    allocate(packetClass);
    synchronized (lock) {
      packetID.id = classInt.get(packetClass);
    }
    socketMonitor.queue(packetID);
  }

  public void allocate(Class<? extends Packet> packetClass) {
    synchronized (lock) {
      if (!classInt.containsKey(packetClass)) {
        i++;
        intClass.put(i, packetClass);
        classInt.put(packetClass, i);
        states.put(packetClass, State.Waiting);
      }
    }
  }
}
