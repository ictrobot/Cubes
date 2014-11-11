package ethanjones.cubes.core.messaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageManager {
  
  private static final HashMap<Object, MessageList> messages = new HashMap<Object, MessageList>();

  public static void sendMessage(Message message, Object object) {
    MessageList list = null;
    synchronized (messages) {
      list = messages.get(object);
      if (list == null) {
        list = new MessageList();
        messages.put(object, list);
      }
    }
    synchronized (list) {
      list.add(message);
    }
  }

  public static List<Message> getMessages(Object object) {
    MessageList list = null;
    synchronized (messages) {
      list = messages.get(object);
      if (list == null) {
        list = new MessageList();
        messages.put(object, list);
      }
    }
    synchronized (list) {
      List<Message> l = new ArrayList<Message>();
      l.addAll(list);
      list.clear();
      return l;
    }
  }

  public static boolean hasMessages(Object object) {
    MessageList list = null;
    synchronized (messages) {
      list = messages.get(object);
      if (list == null) {
        list = new MessageList();
        messages.put(object, list);
      }
    }
    synchronized (list) {
      return list.size() > 0;
    }
  }

  public static void waitForMessage(Object object, int waitMS) {
    int ms = 0;
    long time = System.currentTimeMillis();
    while (ms < waitMS) {
      if (hasMessages(object)) {
        return;
      }
      try {
        Thread.sleep(Math.min(waitMS - ms, 10));
      } catch (InterruptedException e) {
      }
      long currentTime = System.currentTimeMillis();
      ms += currentTime - time;
      time = currentTime;
    }
  }
}
