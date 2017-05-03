package ethanjones.cubes.core.performance;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.system.Debug;

import com.badlogic.gdx.files.FileHandle;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Performance {

  public static long startTime = System.nanoTime();
  private static ThreadLocal<ThreadPerformance> threadNode = new ThreadLocalPerformance();
  private static final ArrayList<ThreadPerformance> nodes = new ArrayList<ThreadPerformance>();
  private static final AtomicBoolean enabled = new AtomicBoolean(false);

  public static void start(String tag) {
    if (!enabled.get()) return;
    ThreadPerformance threadPerformance = threadNode.get();
    synchronized (threadPerformance) {
      PerformanceNode last = threadPerformance.active.getLast();
      PerformanceNode n = new PerformanceNode();
      n.tag = tag;
      n.parent = last;
      last.children.add(n);
      threadPerformance.active.add(n);
      n.start = System.nanoTime() - startTime;
    }
  }

  public static void stop(String tag) {
    if (!enabled.get()) return;
    ThreadPerformance threadPerformance = threadNode.get();
    synchronized (threadPerformance) {
      PerformanceNode last = threadPerformance.active.getLast();
      if (last.tag.equals(tag) && !(last instanceof ThreadPerformance)) {
        last.end = System.nanoTime() - startTime;
        threadPerformance.active.remove(last);
      } else {
        Log.error("Performance: stopping tag '" + tag + "' when the last tag is '" + last.tag + "'");
        PerformanceNode node = null;
        for (PerformanceNode performanceNode : threadPerformance.active) {
          if (performanceNode.tag.equals(tag)) {
            node = performanceNode;
            break;
          }
        }
        if (node == null) {
          Log.error("Performance: tag '" + tag + "' was never started");
          return;
        }
        ArrayDeque<PerformanceNode> d = new ArrayDeque<PerformanceNode>();
        d.add(node);
        while (d.size() > 0) {
          PerformanceNode n = d.getFirst();
          if (n.end == 0) {
            n.end = System.nanoTime() - startTime;
            threadPerformance.active.remove(n);
            d.addAll(n.children);
          }
        }
      }
    }
  }

  public static synchronized void startTracking() {
    startTime = System.nanoTime();
    enabled.set(true);
    Log.warning("Started performance tracking");
  }

  public static synchronized void stopTracking() {
    enabled.set(false);
    synchronized (nodes) {
      for (ThreadPerformance node : nodes) {
        synchronized (node) {
          Iterator<PerformanceNode> iterator = node.active.iterator();
          while (iterator.hasNext()) {
            iterator.next().end = System.nanoTime() - startTime;
            iterator.remove();
          }
        }
      }
    }
    Log.warning("Stopped performance tracking");
  }

  public static synchronized void toggleTracking() {
    if (enabled.get()) {
      stopTracking();
      FileHandle dir = Compatibility.get().getBaseFolder().child("performance");
      Compatibility.get().nomedia(dir);
      dir.mkdirs();
      try {
        save(dir.child(System.currentTimeMillis() + ".cbpf").file());
      } catch (IOException e) {
        Debug.crash(e);
      }
    } else {
      clear();
      startTracking();
    }
  }

  public static synchronized void clear() {
    if (enabled.get()) return;
    synchronized (nodes) {
      nodes.clear();
      threadNode = new ThreadLocalPerformance();
    }
  }

  public static synchronized void save(File file) throws IOException {
    if (!file.exists()) file.createNewFile();
    FileOutputStream fileOutputStream = null;
    try {
      fileOutputStream = new FileOutputStream(file);
      DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
      dataOutputStream.writeByte(0xEE);
      dataOutputStream.writeByte(0xCE);
      synchronized (nodes) {
        for (ThreadPerformance threadN : nodes) {
          dataOutputStream.writeByte(0x00);
          write(threadN, dataOutputStream);
        }
      }
      dataOutputStream.writeByte(0xFF);
    } finally {
      if (fileOutputStream != null) {
        try {
          fileOutputStream.close();
        } catch (Exception ignored) {
      
        }
      }
    }
    Log.info("Saved performance information '" + file.getAbsolutePath() + "'");
  }

  private static void write(PerformanceNode node, DataOutputStream dataOutputStream) throws IOException {
    dataOutputStream.writeUTF(node.tag);
    dataOutputStream.writeLong(node.start);
    dataOutputStream.writeLong(node.end);
    for (PerformanceNode child : node.children) {
      dataOutputStream.writeByte(0x00);
      write(child, dataOutputStream);
    }
    dataOutputStream.writeByte(0xFF);
  }

  public static class PerformanceNode {
    String tag;
    long start;
    long end;
    PerformanceNode parent = null;
    ArrayList<PerformanceNode> children = new ArrayList<PerformanceNode>();
  }

  public static class ThreadPerformance extends PerformanceNode {
    LinkedList<PerformanceNode> active = new LinkedList<PerformanceNode>() {{
      add(ThreadPerformance.this);
    }};
  }

  public static class ThreadLocalPerformance extends ThreadLocal<ThreadPerformance> {
    @Override
    protected ThreadPerformance initialValue() {
      ThreadPerformance threadPerformance = new ThreadPerformance();
      threadPerformance.tag = Thread.currentThread().getName() + "|" + Thread.currentThread().getId();
      threadPerformance.start = System.nanoTime() - startTime;
      synchronized (nodes) {
        nodes.add(threadPerformance);
      }
      return threadPerformance;
    }
  }
}
