package ethanjones.modularworld.networking;

import com.badlogic.gdx.net.Socket;
import ethanjones.modularworld.core.data.ByteBase;
import ethanjones.modularworld.core.logging.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SocketMonitor extends SocketMonitorBase {

  public Socket socket;
  InputStream inputStream;
  OutputStream outputStream;

  public SocketMonitor(Socket socket) {
    super(socket.getRemoteAddress());
    this.socket = socket;
    inputStream = socket.getInputStream();
    outputStream = socket.getOutputStream();
  }

  @Override
  public void run() {
    byte[] b = new byte[256];
    while (running) {
      try {
        int k = inputStream.read(b);
        NetworkingManager.clientNetworking.received(ByteBase.decompress(b), this);
      } catch (Exception e) {
        if (running) Log.error(e);
      }
    }
    dispose();
  }

  protected synchronized void send(byte[] b) {
    try {
      outputStream.write(b);
    } catch (Exception e) {
      if (running) Log.error(e);
    }
  }

  @Override
  public void dispose() {
    running = false;
    try {
      inputStream.close();
      outputStream.close();
    } catch (IOException e) {

    }
    socket.dispose();
    getThread().interrupt();
  }
}
