package ethanjones.modularworld.networking;

import com.badlogic.gdx.net.Socket;
import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.core.data.ByteBase;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SocketMonitor implements Runnable {

  public boolean running;
  public Socket socket;
  InputStream inputStream;
  OutputStream outputStream;

  public SocketMonitor(Socket socket) {
    running = true;
    inputStream = socket.getInputStream();
    outputStream = socket.getOutputStream();
  }

  @Override
  public void run() {
    byte[] b = new byte[256];
    while (running) {
      try {
        int k = inputStream.read(b);
        ModularWorld.instance.networking.received(ByteBase.decompress(b), this);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  protected synchronized void send(byte[] b) {
    try {
      outputStream.write(b);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
